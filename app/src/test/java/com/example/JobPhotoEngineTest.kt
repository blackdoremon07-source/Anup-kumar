package com.example

import com.example.jobphoto.data.InMemoryRecruitmentProfileRepository
import com.example.jobphoto.data.RecruitmentProfilesData
import com.example.jobphoto.engine.JobPhotoEngine
import com.example.jobphoto.model.OfficialSource
import com.example.jobphoto.model.PhotoRequirement
import com.example.jobphoto.model.RecruitmentCategory
import com.example.jobphoto.model.RecruitmentProfile
import com.example.jobphoto.model.SignatureRequirement
import com.example.jobphoto.model.ValidationCheckStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class JobPhotoEngineTest {

    @Test
    fun testRecruitmentProfilesDataCompleteness() {
        val profiles = RecruitmentProfilesData.verifiedProfiles
        assertTrue("Profiles should have multiple entries", profiles.size >= 8)

        // Test SSC Profile existence and specs
        val ssc = profiles.find { it.category == RecruitmentCategory.SSC }
        assertNotNull(ssc)
        assertEquals("JPG / JPEG", ssc!!.photoRequirement.requiredFormat)
        assertTrue(ssc.photoRequirement.minKb in 10..30)
        assertTrue(ssc.photoRequirement.maxKb in 40..60)
        assertTrue(ssc.officialSource.isVerified)

        // Test UPSC Profile existence and specs
        val upsc = profiles.find { it.category == RecruitmentCategory.UPSC }
        assertNotNull(upsc)
        assertEquals("1 : 1", upsc!!.photoRequirement.aspectRatio)

        // Test Unverified Exam Profile notice
        val unverified = profiles.find { !it.officialSource.isVerified }
        assertNotNull(unverified)
        assertEquals(
            "Official requirement not verified yet. Do not present guessed values as official.",
            unverified!!.officialSource.unverifiedNotice
        )
    }

    @Test
    fun testRepositorySearchAndFiltering() = runBlocking {
        val repo = InMemoryRecruitmentProfileRepository()

        // 1. Search for "SSC"
        val sscResults = repo.searchProfiles("SSC").first()
        assertTrue(sscResults.isNotEmpty())
        assertTrue(sscResults.all { it.name.contains("SSC") || it.category == RecruitmentCategory.SSC || it.organization.contains("SSC") })

        // 2. Filter by BANKING category
        val bankingResults = repo.getProfilesByCategory(RecruitmentCategory.BANKING).first()
        assertTrue(bankingResults.isNotEmpty())
        assertTrue(bankingResults.all { it.category == RecruitmentCategory.BANKING })

        // 3. Admin CRUD: Add new profile
        val customProfile = RecruitmentProfile(
            id = "admin_test_exam",
            name = "Test Admin Recruitment 2026",
            organization = "Test Agency",
            category = RecruitmentCategory.OTHER,
            photoRequirement = PhotoRequirement(width = 300, height = 300),
            signatureRequirement = SignatureRequirement(width = 200, height = 100),
            documentRequirement = com.example.jobphoto.model.DocumentRequirement(),
            officialSource = OfficialSource(
                sourceName = "Test Gazette",
                sourceUrl = "https://test.gov.in",
                notificationTitleOrNumber = "Notice 01/2026",
                isVerified = true
            )
        )
        repo.addProfile(customProfile)
        val fetched = repo.getProfileById("admin_test_exam").first()
        assertNotNull(fetched)
        assertEquals("Test Admin Recruitment 2026", fetched?.name)

        // 4. Admin CRUD: Delete profile
        repo.deleteProfile("admin_test_exam")
        val afterDelete = repo.getProfileById("admin_test_exam").first()
        assertEquals(null, afterDelete)
    }

    @Test
    fun testAutoFixPhotoAndSignatureEngine() = runBlocking {
        val sscProfile = RecruitmentProfilesData.verifiedProfiles.first { it.id == "ssc_cgl_chsl" }

        // Generate sample photo & auto-fix to SSC requirement
        val samplePhoto = JobPhotoEngine.generateSampleSignature(600, 800)
        val fixedPhoto = JobPhotoEngine.autoFixPhoto(samplePhoto, sscProfile.photoRequirement)

        assertNotNull(fixedPhoto)
        assertEquals(sscProfile.photoRequirement.width, fixedPhoto.width)
        assertEquals(sscProfile.photoRequirement.height, fixedPhoto.height)

        // Test compression to target KB range
        val (compressedBytes, quality) = JobPhotoEngine.compressToRange(
            fixedPhoto,
            sscProfile.photoRequirement.minKb,
            sscProfile.photoRequirement.maxKb
        )
        assertTrue("Compressed bytes should not be empty", compressedBytes.isNotEmpty())
        assertTrue("Quality should be positive", quality > 0)

        // Generate sample signature & auto-fix to SSC requirement
        val sampleSig = JobPhotoEngine.generateSampleSignature(500, 250)
        val fixedSig = JobPhotoEngine.autoFixSignature(sampleSig, sscProfile.signatureRequirement)

        assertNotNull(fixedSig)
        assertEquals(sscProfile.signatureRequirement.width, fixedSig.width)
        assertEquals(sscProfile.signatureRequirement.height, fixedSig.height)
    }

    @Test
    fun testValidationComplianceReport() = runBlocking {
        val profile = RecruitmentProfilesData.verifiedProfiles.first { it.id == "ssc_cgl_chsl" }
        val (samplePhoto, _) = com.example.editor.engine.ImageEngine.generateSampleCandidatePhoto(
            profile.photoRequirement.width,
            profile.photoRequirement.height
        )
        val sampleSig = JobPhotoEngine.generateSampleSignature(
            profile.signatureRequirement.width,
            profile.signatureRequirement.height
        )

        val (photoBytes, _) = JobPhotoEngine.compressToRange(
            samplePhoto,
            profile.photoRequirement.minKb,
            profile.photoRequirement.maxKb
        )
        val (sigBytes, _) = JobPhotoEngine.compressToRange(
            sampleSig,
            profile.signatureRequirement.minKb,
            profile.signatureRequirement.maxKb
        )

        val report = JobPhotoEngine.validateJobSubmission(
            photoBitmap = samplePhoto,
            photoBytes = photoBytes,
            photoReq = profile.photoRequirement,
            sigBitmap = sampleSig,
            sigBytes = sigBytes,
            sigReq = profile.signatureRequirement
        )

        assertEquals(ValidationCheckStatus.PASS, report.photoDimensions.status)
        assertEquals(ValidationCheckStatus.PASS, report.signatureDimensions.status)
        assertNotNull(report.photoFileSize.status)
    }

    @Test
    fun testZipApplicationKitCreation() = runBlocking {
        val profile = RecruitmentProfilesData.verifiedProfiles.first()
        val dummyPhoto = "Dummy Photo Bytes".toByteArray()
        val dummySig = "Dummy Signature Bytes".toByteArray()

        val zipBytes = JobPhotoEngine.createApplicationKitZip(dummyPhoto, dummySig, profile)
        assertNotNull(zipBytes)
        assertTrue(zipBytes.isNotEmpty())

        // Verify ZIP entries
        val entryNames = mutableListOf<String>()
        ZipInputStream(ByteArrayInputStream(zipBytes)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                entryNames.add(entry.name)
                entry = zis.nextEntry
            }
        }

        assertTrue(entryNames.contains("DG_with_Anup_Photo.jpg"))
        assertTrue(entryNames.contains("DG_with_Anup_Signature.jpg"))
        assertTrue(entryNames.contains("Requirements_Summary.txt"))
    }
}
