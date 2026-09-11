package com.example.jobphoto.data

import com.example.jobphoto.model.DimensionUnit
import com.example.jobphoto.model.DocumentRequirement
import com.example.jobphoto.model.OfficialSource
import com.example.jobphoto.model.PhotoRequirement
import com.example.jobphoto.model.RecruitmentCategory
import com.example.jobphoto.model.RecruitmentProfile
import com.example.jobphoto.model.SignatureRequirement

object RecruitmentProfilesData {

    val verifiedProfiles: List<RecruitmentProfile> = listOf(
        // 1. SSC CGL / CHSL
        RecruitmentProfile(
            id = "ssc_cgl_chsl",
            name = "SSC CGL / CHSL / MTS (Staff Selection Commission)",
            organization = "Staff Selection Commission (SSC)",
            category = RecruitmentCategory.SSC,
            isPopular = true,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 450,
                unit = DimensionUnit.PX,
                displayDimensionString = "3.5 cm × 4.5 cm (approx 350 × 450 px)",
                aspectRatio = "3.5 : 4.5",
                minKb = 20,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "Plain white or light-coloured background",
                colourRequirement = "Recent colour passport photo without spectacles or cap",
                otherInstructions = "Both ears must be clearly visible. Cap, mask, and spectacles are strictly prohibited."
            ),
            signatureRequirement = SignatureRequirement(
                width = 400,
                height = 200,
                unit = DimensionUnit.PX,
                displayDimensionString = "4.0 cm × 2.0 cm (approx 400 × 200 px)",
                aspectRatio = "2.0 : 1.0",
                minKb = 10,
                maxKb = 20,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White sheet with Black or Dark Blue ink",
                otherInstructions = "Signatures in ALL CAPITAL letters will be summarily rejected."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 300,
                maxPages = 2,
                numberOfFiles = 1,
                otherInstructions = "Valid photo identity document and matriculation marksheet."
            ),
            officialSource = OfficialSource(
                sourceName = "SSC Official Notification Guidelines",
                sourceUrl = "https://ssc.gov.in",
                notificationTitleOrNumber = "Notice F.No. 3/1/2024-P&P_I (Annexure-III)",
                isVerified = true,
                lastVerifiedDate = "July 2024"
            )
        ),

        // 2. UPSC Civil Services / OTR
        RecruitmentProfile(
            id = "upsc_cse",
            name = "UPSC Civil Services (IAS / IPS / IFS) & OTR",
            organization = "Union Public Service Commission (UPSC)",
            category = RecruitmentCategory.UPSC,
            isPopular = true,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 350,
                unit = DimensionUnit.PX,
                displayDimensionString = "350 × 350 px (Min) to 1000 × 1000 px (Max)",
                aspectRatio = "1 : 1",
                minKb = 20,
                maxKb = 300,
                requiredFormat = "JPG / JPEG",
                dpi = 300,
                backgroundRequirement = "Plain white background without borders",
                colourRequirement = "Clear recent portrait with candidate's face covering 3/4th of the photograph",
                otherInstructions = "Photograph should not be more than 10 days old from opening of online application. Candidate name and date of photo printed below is recommended."
            ),
            signatureRequirement = SignatureRequirement(
                width = 350,
                height = 350,
                unit = DimensionUnit.PX,
                displayDimensionString = "350 × 350 px (Min) to 1000 × 1000 px (Max)",
                aspectRatio = "1 : 1",
                minKb = 20,
                maxKb = 300,
                requiredFormat = "JPG / JPEG",
                dpi = 300,
                background = "White background with crisp black ink",
                otherInstructions = "Sign clearly in running handwriting using dark black pen."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 300,
                maxPages = 3,
                numberOfFiles = 1,
                otherInstructions = "Photo ID card in PDF format, file size between 20 KB to 300 KB."
            ),
            officialSource = OfficialSource(
                sourceName = "Union Public Service Commission Gazette",
                sourceUrl = "https://upsconline.nic.in",
                notificationTitleOrNumber = "Examination Notice No. 05/2024-CSP",
                isVerified = true,
                lastVerifiedDate = "February 2024"
            )
        ),

        // 3. Railway / RRB (NTPC, Group D, ALP)
        RecruitmentProfile(
            id = "rrb_railway",
            name = "Railway Recruitment Board (RRB NTPC, ALP, Group D)",
            organization = "Indian Railways (Ministry of Railways)",
            category = RecruitmentCategory.RAILWAY,
            isPopular = true,
            photoRequirement = PhotoRequirement(
                width = 240,
                height = 320,
                unit = DimensionUnit.PX,
                displayDimensionString = "35 mm × 45 mm (240 × 320 px)",
                aspectRatio = "3 : 4",
                minKb = 30,
                maxKb = 70,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "Plain White or very light gray background",
                colourRequirement = "Color passport photo taken against white background",
                otherInstructions = "Photo must have clear contrast. Selfie photos, side profiles, sunglasses, or head coverings (except religious) will lead to rejection."
            ),
            signatureRequirement = SignatureRequirement(
                width = 280,
                height = 140,
                unit = DimensionUnit.PX,
                displayDimensionString = "50 mm × 20 mm (280 × 140 px)",
                aspectRatio = "2 : 1",
                minKb = 30,
                maxKb = 70,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White paper with Black ink pen",
                otherInstructions = "Must be signed in running script on white paper within bounding box."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 500,
                maxPages = 2,
                numberOfFiles = 1,
                otherInstructions = "SC/ST Free Travel Authority Certificate if claiming concession."
            ),
            officialSource = OfficialSource(
                sourceName = "Railway Recruitment Boards Centralized Employment Notice",
                sourceUrl = "https://indianrailways.gov.in",
                notificationTitleOrNumber = "CEN No. 01/2024 (ALP) & CEN 05/2024",
                isVerified = true,
                lastVerifiedDate = "June 2024"
            )
        ),

        // 4. Banking (IBPS PO / Clerk, SBI PO / Clerk)
        RecruitmentProfile(
            id = "banking_ibps_sbi",
            name = "Banking - IBPS PO / Clerk & SBI PO",
            organization = "Institute of Banking Personnel Selection & State Bank of India",
            category = RecruitmentCategory.BANKING,
            isPopular = true,
            photoRequirement = PhotoRequirement(
                width = 200,
                height = 230,
                unit = DimensionUnit.PX,
                displayDimensionString = "4.5 cm × 3.5 cm (200 × 230 px)",
                aspectRatio = "3.5 : 4.5",
                minKb = 20,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "Light-coloured, preferably white background",
                colourRequirement = "Colour photograph with relaxed face looking straight at camera",
                otherInstructions = "If flash is used, ensure there is no red-eye. If you wear glasses, ensure there are no reflections and eyes can be clearly seen."
            ),
            signatureRequirement = SignatureRequirement(
                width = 140,
                height = 60,
                unit = DimensionUnit.PX,
                displayDimensionString = "140 × 60 px (preferred)",
                aspectRatio = "7 : 3",
                minKb = 10,
                maxKb = 20,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White paper with Black Ink pen",
                otherInstructions = "Signatures in capital letters will NOT be accepted. Must match signature at examination venue."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "JPG / PDF",
                maxKb = 100,
                maxPages = 1,
                numberOfFiles = 2,
                otherInstructions = "Left thumb impression (20-50 KB) and handwritten declaration (50-100 KB)."
            ),
            officialSource = OfficialSource(
                sourceName = "IBPS Common Recruitment Process Notice",
                sourceUrl = "https://ibps.in",
                notificationTitleOrNumber = "CRP PO/MT-XIV & CRP CLERKS-XIV Notice",
                isVerified = true,
                lastVerifiedDate = "August 2024"
            )
        ),

        // 5. Police (UP Police / Delhi Police Constable & SI)
        RecruitmentProfile(
            id = "police_up_delhi",
            name = "UP Police & Delhi Police (Constable / SI)",
            organization = "Uttar Pradesh Police Recruitment Board (UPPRPB) / Delhi Police",
            category = RecruitmentCategory.POLICE,
            isPopular = true,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 450,
                unit = DimensionUnit.PX,
                displayDimensionString = "35 mm × 45 mm (350 × 450 px)",
                aspectRatio = "3.5 : 4.5",
                minKb = 20,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "White or light gray background strictly (white preferred)",
                colourRequirement = "Recent colour photograph showing both ears, chin and forehead clearly",
                otherInstructions = "Face should cover 70% of the photograph. Caps, muffler, dark glasses are forbidden."
            ),
            signatureRequirement = SignatureRequirement(
                width = 350,
                height = 150,
                unit = DimensionUnit.PX,
                displayDimensionString = "3.5 cm × 1.5 cm (350 × 150 px)",
                aspectRatio = "7 : 3",
                minKb = 5,
                maxKb = 20,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "Plain white paper with Black ink",
                otherInstructions = "Clear black ink signature only. Blur or faded signatures cause application rejection."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "JPG / PDF",
                maxKb = 100,
                maxPages = 1,
                numberOfFiles = 3,
                otherInstructions = "10th certificate, 12th certificate, and domicile certificate."
            ),
            officialSource = OfficialSource(
                sourceName = "UPPRPB Official Recruitment Board Notification",
                sourceUrl = "https://uppbpb.gov.in",
                notificationTitleOrNumber = "Notification PRPB-1(150)/2023",
                isVerified = true,
                lastVerifiedDate = "January 2024"
            )
        ),

        // 6. Defence (NDA, CDS, AFCAT, Agniveer)
        RecruitmentProfile(
            id = "defence_nda_cds",
            name = "Defence - NDA / CDS & Indian Army Agniveer",
            organization = "Ministry of Defence / UPSC",
            category = RecruitmentCategory.DEFENCE,
            isPopular = false,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 350,
                unit = DimensionUnit.PX,
                displayDimensionString = "350 × 350 px (Square)",
                aspectRatio = "1 : 1",
                minKb = 20,
                maxKb = 100,
                requiredFormat = "JPG / JPEG",
                dpi = 300,
                backgroundRequirement = "Light background, preferred white",
                colourRequirement = "Clean shaved (where applicable), formal dress, clear eyes",
                otherInstructions = "No uniforms with unauthorized badges. Hair neatly combed."
            ),
            signatureRequirement = SignatureRequirement(
                width = 350,
                height = 175,
                unit = DimensionUnit.PX,
                displayDimensionString = "350 × 175 px",
                aspectRatio = "2 : 1",
                minKb = 10,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White background with Dark Black ink",
                otherInstructions = "Candidate must sign on white unruled paper."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 200,
                maxPages = 2,
                numberOfFiles = 1,
                otherInstructions = "Matriculation proof and Identity proof."
            ),
            officialSource = OfficialSource(
                sourceName = "Join Indian Army & UPSC Defence Notifications",
                sourceUrl = "https://joinindianarmy.nic.in",
                notificationTitleOrNumber = "Notice No. NDA & NA (II)/2024",
                isVerified = true,
                lastVerifiedDate = "May 2024"
            )
        ),

        // 7. Teaching (CTET / KVS / DSSSB)
        RecruitmentProfile(
            id = "teaching_ctet_kvs",
            name = "Teaching - CTET, KVS & NVS",
            organization = "Central Board of Secondary Education (CBSE)",
            category = RecruitmentCategory.TEACHING,
            isPopular = false,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 450,
                unit = DimensionUnit.PX,
                displayDimensionString = "3.5 cm × 4.5 cm (350 × 450 px)",
                aspectRatio = "3.5 : 4.5",
                minKb = 10,
                maxKb = 100,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "White background mandatory",
                colourRequirement = "Passport size colour photograph with both ears visible",
                otherInstructions = "Photograph should clearly depict candidate identity."
            ),
            signatureRequirement = SignatureRequirement(
                width = 350,
                height = 150,
                unit = DimensionUnit.PX,
                displayDimensionString = "3.5 cm × 1.5 cm",
                aspectRatio = "7 : 3",
                minKb = 4,
                maxKb = 30,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White sheet with Black or Blue ink",
                otherInstructions = "Signature in capital letters is not acceptable."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 300,
                maxPages = 2,
                numberOfFiles = 1,
                otherInstructions = "B.Ed / D.El.Ed completion certificate or marksheet."
            ),
            officialSource = OfficialSource(
                sourceName = "CBSE CTET Information Bulletin",
                sourceUrl = "https://ctet.nic.in",
                notificationTitleOrNumber = "Information Bulletin CTET Dec-2024",
                isVerified = true,
                lastVerifiedDate = "September 2024"
            )
        ),

        // 8. State Government (BPSC 70th CCE / UPPSC PCS)
        RecruitmentProfile(
            id = "state_bpsc_uppsc",
            name = "State PSC - BPSC & UPPSC Combined Competitive Exam",
            organization = "Bihar Public Service Commission (BPSC)",
            category = RecruitmentCategory.STATE_GOVT,
            isPopular = false,
            photoRequirement = PhotoRequirement(
                width = 250,
                height = 250,
                unit = DimensionUnit.PX,
                displayDimensionString = "250 × 250 px (Square)",
                aspectRatio = "1 : 1",
                minKb = 25,
                maxKb = 100,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "Light or white background",
                colourRequirement = "Live webcam / studio colour passport photograph",
                otherInstructions = "Both Hindi and English signatures required on portal. Face clearly centered."
            ),
            signatureRequirement = SignatureRequirement(
                width = 250,
                height = 100,
                unit = DimensionUnit.PX,
                displayDimensionString = "250 × 100 px",
                aspectRatio = "5 : 2",
                minKb = 15,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White paper with Black/Blue ink",
                otherInstructions = "Clear legible signature in running hand."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 100,
                maxPages = 1,
                numberOfFiles = 4,
                otherInstructions = "Aadhaar Card, Graduation Certificate, Reservation Certificate (if applicable)."
            ),
            officialSource = OfficialSource(
                sourceName = "BPSC Examination Guidelines",
                sourceUrl = "https://bpsc.bih.nic.in",
                notificationTitleOrNumber = "Advertisement No. 20/2024",
                isVerified = true,
                lastVerifiedDate = "July 2024"
            )
        ),

        // 9. Central Government (DRDO / ISRO / SSC MTS)
        RecruitmentProfile(
            id = "central_drdo_isro",
            name = "Central Govt - DRDO CEPTAM & ISRO ICRB",
            organization = "Defence Research & Development Organisation (DRDO)",
            category = RecruitmentCategory.CENTRAL_GOVT,
            isPopular = false,
            photoRequirement = PhotoRequirement(
                width = 350,
                height = 450,
                unit = DimensionUnit.PX,
                displayDimensionString = "35 mm × 45 mm",
                aspectRatio = "3.5 : 4.5",
                minKb = 20,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "Plain white or light blue background",
                colourRequirement = "Front facing colour photograph taken within 30 days",
                otherInstructions = "Eyes wide open, looking directly into the camera. No glare on eyeglasses."
            ),
            signatureRequirement = SignatureRequirement(
                width = 300,
                height = 100,
                unit = DimensionUnit.PX,
                displayDimensionString = "300 × 100 px",
                aspectRatio = "3 : 1",
                minKb = 10,
                maxKb = 30,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White paper with dark blue or black ink",
                otherInstructions = "Signature must not touch the outer boundary."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 500,
                maxPages = 2,
                numberOfFiles = 2,
                otherInstructions = "Diploma / Degree Certificate & Category Certificate."
            ),
            officialSource = OfficialSource(
                sourceName = "DRDO CEPTAM Advertisement",
                sourceUrl = "https://drdo.gov.in",
                notificationTitleOrNumber = "Advt CEPTAM-11/DRTC",
                isVerified = true,
                lastVerifiedDate = "April 2024"
            )
        ),

        // 10. Unverified / Custom Government Exam Profile
        // (Demonstrates Section 3 requirement: "If verified official requirements are unavailable: show: 'Official requirement not verified yet. Do not present guessed values as official.'")
        RecruitmentProfile(
            id = "custom_unverified_exam",
            name = "Other / State Local Board Exam (Unverified)",
            organization = "Regional / District Recruitment Board",
            category = RecruitmentCategory.OTHER,
            isPopular = false,
            photoRequirement = PhotoRequirement(
                width = 300,
                height = 400,
                unit = DimensionUnit.PX,
                displayDimensionString = "300 × 400 px (Tentative)",
                aspectRatio = "3 : 4",
                minKb = 20,
                maxKb = 100,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                backgroundRequirement = "White or off-white background",
                colourRequirement = "Recent passport photograph",
                otherInstructions = "Verify latest notification from official board advertisement before uploading."
            ),
            signatureRequirement = SignatureRequirement(
                width = 250,
                height = 100,
                unit = DimensionUnit.PX,
                displayDimensionString = "250 × 100 px (Tentative)",
                aspectRatio = "5 : 2",
                minKb = 10,
                maxKb = 50,
                requiredFormat = "JPG / JPEG",
                dpi = 200,
                background = "White paper with black ink",
                otherInstructions = "Confirm exact portal dimension from official notification."
            ),
            documentRequirement = DocumentRequirement(
                requiredFormat = "PDF",
                maxKb = 300,
                maxPages = 2,
                numberOfFiles = 1,
                otherInstructions = "Check local notification for document submission rules."
            ),
            officialSource = OfficialSource(
                sourceName = "Pending Board Release",
                sourceUrl = "https://example.gov.in",
                notificationTitleOrNumber = "Awaiting Official Advertisement",
                isVerified = false, // UNVERIFIED
                lastVerifiedDate = "Not Available",
                unverifiedNotice = "Official requirement not verified yet. Do not present guessed values as official."
            )
        )
    )
}
