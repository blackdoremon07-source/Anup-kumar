package com.example.data

import com.example.model.AdmitCard
import com.example.model.AnswerKey
import com.example.model.GovernmentScheme
import com.example.model.Job
import com.example.model.JobCategory
import com.example.model.Notification
import com.example.model.NotificationImportance
import com.example.model.Result
import com.example.model.Tool

/**
 * ============================================================================
 * PLACEHOLDER / SEED DATA REPOSITORY
 * NOTE FOR PART 7:
 * This in-memory mock repository contains realistic placeholder structures.
 * In Part 7, these functions will connect directly to Room / Firebase database.
 * DO NOT rely on this data for official government representation.
 * ============================================================================
 */
object MockDataProvider {

    val sampleJobs: List<Job> = listOf(
        Job(
            id = "job-ssc-cgl-2026",
            title = "Combined Graduate Level (CGL) Examination",
            organization = "Staff Selection Commission (SSC)",
            category = JobCategory.SSC,
            lastDate = "28 Oct 2026",
            qualification = "Bachelor's Degree in Any Stream",
            totalVacancies = "14,500+ Posts",
            salary = "Level 4 to Level 8 (₹25,500 - ₹1,51,100)",
            location = "All India",
            status = "Active",
            ageLimit = "18 - 32 Years (Relaxations applicable)",
            applicationFee = "₹100 (SC/ST/Female: Exempted)",
            description = "Recruitment to Group 'B' and 'C' Gazetted and Non-Gazetted positions across various Central Ministries, Departments, and Attached Offices."
        ),
        Job(
            id = "job-upsc-cse-2026",
            title = "Civil Services Examination (CSE)",
            organization = "Union Public Service Commission (UPSC)",
            category = JobCategory.UPSC,
            lastDate = "15 Nov 2026",
            qualification = "Graduate Degree from Recognized University",
            totalVacancies = "1,150 Posts",
            salary = "Pay Level 10 (Starting Basic ₹56,100 + DA/HRA)",
            location = "All India / All Cadres",
            status = "New",
            ageLimit = "21 - 32 Years",
            applicationFee = "₹100 (Female/SC/ST/PwBD: Free)",
            description = "Apex civil services examination for induction into Indian Administrative Service (IAS), Indian Police Service (IPS), IFS, and Central Group A Services."
        ),
        Job(
            id = "job-rrb-ntpc-2026",
            title = "Non-Technical Popular Categories (NTPC)",
            organization = "Railway Recruitment Boards (RRB)",
            category = JobCategory.RAILWAY,
            lastDate = "05 Nov 2026",
            qualification = "12th Pass or Any Graduate",
            totalVacancies = "11,558 Posts",
            salary = "Pay Level 2 to Level 6 (₹19,900 - ₹35,400 Basic)",
            location = "All Railway Zones Across India",
            status = "Active",
            ageLimit = "18 - 36 Years (3 yrs pandemic relaxation included)",
            applicationFee = "₹500 (₹400 refundable after CBT-1)",
            description = "Recruitment for Station Master, Goods Train Manager, Senior Commercial Clerk, Junior Accounts Assistant, and Ticket Clerks."
        ),
        Job(
            id = "job-ibps-po-2026",
            title = "Probationary Officers / Management Trainees (PO-XIV)",
            organization = "Institute of Banking Personnel Selection (IBPS)",
            category = JobCategory.BANKING,
            lastDate = "20 Oct 2026",
            qualification = "Graduation in Any Discipline",
            totalVacancies = "4,450 Posts",
            salary = "Approx. ₹52,000 - ₹55,000 in hand per month",
            location = "Pan-India Participating PSBs",
            status = "Ending Soon",
            ageLimit = "20 - 30 Years",
            applicationFee = "₹850 (₹175 for SC/ST/PwBD)",
            description = "Common Recruitment Process for Public Sector Banks including PNB, Bank of Baroda, Canara Bank, Union Bank, and Indian Bank."
        ),
        Job(
            id = "job-police-si-2026",
            title = "Sub-Inspector of Police & Platoon Commander",
            organization = "State Police Recruitment Board",
            category = JobCategory.POLICE,
            lastDate = "30 Nov 2026",
            qualification = "Graduate Degree + Physical Fitness Standards",
            totalVacancies = "3,200 Posts",
            salary = "Pay Scale ₹35,400 - ₹1,12,400 (Level 6)",
            location = "State Wide Police Stations & Battalions",
            status = "Active",
            ageLimit = "21 - 28 Years",
            applicationFee = "₹400",
            description = "Direct recruitment for executive Sub-Inspectors. Involves Written Exam, Physical Measurement Test (PMT), PET, and Medical Examination."
        ),
        Job(
            id = "job-defence-army-2026",
            title = "Indian Army Technical & General Entry Rally",
            organization = "Ministry of Defence / Indian Army",
            category = JobCategory.DEFENCE,
            lastDate = "10 Dec 2026",
            qualification = "10th / 12th Pass with Science (PCM)",
            totalVacancies = "25,000+ Enrolments",
            salary = "Structured Package + Seva Nidhi Package",
            location = "All Zonal Recruiting Offices (ZRO)",
            status = "New",
            ageLimit = "17.5 - 21 Years",
            applicationFee = "Nil",
            description = "Rally recruitment scheme for All Arms and Services with Computer-Based Entrance Examination (CEE) followed by physical endurance rally."
        ),
        Job(
            id = "job-kvs-teaching-2026",
            title = "Post Graduate Teachers (PGT) & TGT Recruitment",
            organization = "Kendriya Vidyalaya Sangathan (KVS)",
            category = JobCategory.TEACHING,
            lastDate = "18 Nov 2026",
            qualification = "Master's Degree in subject + B.Ed / CTET qualified",
            totalVacancies = "6,800 Posts",
            salary = "Level 7 & 8 (₹47,600 - ₹1,51,100)",
            location = "Kendriya Vidyalayas Nationwide",
            status = "Active",
            ageLimit = "Up to 40 Years (Age relaxation for women: 10 yrs)",
            applicationFee = "₹1500 (SC/ST/PwD: Nil)",
            description = "Recruitment of subject teachers across English, Hindi, Mathematics, Physics, Chemistry, Biology, and Computer Science."
        ),
        Job(
            id = "job-state-psc-2026",
            title = "Combined State Civil Services Examination",
            organization = "State Public Service Commission",
            category = JobCategory.STATE_GOVT,
            lastDate = "12 Nov 2026",
            qualification = "Graduation in Any Stream",
            totalVacancies = "820 Posts",
            salary = "Level 9 & 10 (₹53,100 - ₹1,67,800)",
            location = "State Administrative Headquarters & Districts",
            status = "Active",
            ageLimit = "21 - 40 Years",
            applicationFee = "₹125",
            description = "Provincial civil services selection for Deputy Collector, Deputy SP, Block Development Officer, Commercial Tax Officer, and Treasury Officer."
        ),
        Job(
            id = "job-central-aso-2026",
            title = "Assistant Section Officer (CSS Cadre)",
            organization = "Department of Personnel & Training (DoPT)",
            category = JobCategory.CENTRAL_GOVT,
            lastDate = "25 Nov 2026",
            qualification = "Bachelor's Degree in Any Discipline",
            totalVacancies = "950 Posts",
            salary = "Level 7 (₹44,900 - ₹1,42,400 Basic)",
            location = "New Delhi (Central Secretariat)",
            status = "New",
            ageLimit = "20 - 30 Years",
            applicationFee = "₹100",
            description = "Desk officers in Central Government Ministries dealing with file processing, policy formulation, parliament questions, and RTI matters."
        )
    )

    val sampleResults: List<Result> = listOf(
        Result(
            id = "res-ssc-chsl-2026",
            title = "SSC CHSL Tier-I Official Merit List & Cut-off Marks",
            organization = "Staff Selection Commission",
            category = JobCategory.SSC,
            declaredDate = "Today, 10:30 AM",
            examDate = "Exam held July-August 2026",
            status = "Declared"
        ),
        Result(
            id = "res-upsc-prelims-2026",
            title = "UPSC Civil Services Preliminary Examination Results",
            organization = "Union Public Service Commission",
            category = JobCategory.UPSC,
            declaredDate = "Yesterday",
            examDate = "Exam held June 2026",
            status = "Declared"
        ),
        Result(
            id = "res-rrb-alp-2026",
            title = "RRB Assistant Loco Pilot (ALP) CBT-1 Qualified List",
            organization = "Railway Recruitment Boards",
            category = JobCategory.RAILWAY,
            declaredDate = "2 Days Ago",
            examDate = "Exam held August 2026",
            status = "Declared"
        ),
        Result(
            id = "res-ibps-clerk-2026",
            title = "IBPS Clerk Preliminary Exam Result Status",
            organization = "IBPS",
            category = JobCategory.BANKING,
            declaredDate = "3 Days Ago",
            examDate = "Exam held Sept 2026",
            status = "Declared"
        )
    )

    val sampleAdmitCards: List<AdmitCard> = listOf(
        AdmitCard(
            id = "ac-ssc-cgl-tier1",
            title = "SSC CGL 2026 Tier-I E-Admit Card & City Intimation",
            organization = "Staff Selection Commission",
            category = JobCategory.SSC,
            examDate = "Starts 14 Oct 2026",
            releaseDate = "Available Now",
            status = "Live"
        ),
        AdmitCard(
            id = "ac-upsc-capf-2026",
            title = "UPSC CAPF (Assistant Commandants) Call Letter",
            organization = "UPSC",
            category = JobCategory.UPSC,
            examDate = "22 Oct 2026",
            releaseDate = "Available Now",
            status = "Live"
        ),
        AdmitCard(
            id = "ac-rrb-ntpc-cbt1",
            title = "RRB NTPC Phase-1 Exam City Slip & Travel Pass",
            organization = "Railway Recruitment Boards",
            category = JobCategory.RAILWAY,
            examDate = "02 Nov 2026",
            releaseDate = "Available Now",
            status = "Live"
        ),
        AdmitCard(
            id = "ac-police-constable",
            title = "State Police Constable PET/PST Physical Admit Card",
            organization = "Police Recruitment Board",
            category = JobCategory.POLICE,
            examDate = "28 Oct 2026",
            releaseDate = "Available Now",
            status = "Live"
        )
    )

    val sampleAnswerKeys: List<AnswerKey> = listOf(
        AnswerKey(
            id = "ak-ssc-gd-2026",
            title = "SSC GD Constable Tentative Answer Key & Response Sheet",
            organization = "Staff Selection Commission",
            category = JobCategory.SSC,
            examDate = "Exam completed Sept 2026",
            objectionLastDate = "Objection Window: Till 24 Oct",
            status = "Out Now"
        ),
        AnswerKey(
            id = "ak-upsc-cds-2026",
            title = "UPSC Combined Defence Services (CDS) Official Keys",
            organization = "Union Public Service Commission",
            category = JobCategory.DEFENCE,
            examDate = "Exam held Sept 2026",
            objectionLastDate = "Review Window Active",
            status = "Out Now"
        ),
        AnswerKey(
            id = "ak-rrb-je-2026",
            title = "RRB Junior Engineer (JE) Stage-1 Question Paper & Key",
            organization = "Railway Recruitment Boards",
            category = JobCategory.RAILWAY,
            examDate = "Exam held August 2026",
            objectionLastDate = "Challenge window open",
            status = "Out Now"
        )
    )

    val sampleNotifications: List<Notification> = listOf(
        Notification(
            id = "notif-ssc-cal-2026",
            title = "SSC Revised Annual Exam Calendar for 2026-27",
            organization = "Staff Selection Commission",
            date = "Oct 2026",
            category = JobCategory.SSC,
            importance = NotificationImportance.URGENT,
            description = "Notification dates, CBT windows, and application timelines updated for CGL, CHSL, MTS, and CPO examinations."
        ),
        Notification(
            id = "notif-upsc-otr",
            title = "Mandatory One-Time Registration (OTR) Advisory",
            organization = "UPSC",
            date = "Oct 2026",
            category = JobCategory.UPSC,
            importance = NotificationImportance.IMPORTANT,
            description = "All aspirants must complete profile verification on upsc.gov.in at least 7 days before filing online forms."
        ),
        Notification(
            id = "notif-rrb-normalization",
            title = "RRB Clarification on Percentile Score Calculation",
            organization = "Ministry of Railways",
            date = "Oct 2026",
            category = JobCategory.RAILWAY,
            importance = NotificationImportance.NORMAL,
            description = "Multi-shift percentile normalization formula details published for upcoming mega railway recruitments."
        ),
        Notification(
            id = "notif-ews-guidelines",
            title = "Official Notification: EWS/OBC-NCL Certificate Financial Year Guidelines",
            organization = "Ministry of Personnel",
            date = "Oct 2026",
            category = JobCategory.CENTRAL_GOVT,
            importance = NotificationImportance.IMPORTANT,
            description = "Certificates must be issued for FY 2025-26 based on income of FY 2024-25 to be valid for all 2026 exams."
        )
    )

    val sampleSchemes: List<GovernmentScheme> = listOf(
        GovernmentScheme(
            id = "scheme-pmkvy",
            title = "PM Kaushal Vikas Yojana (PMKVY 4.0)",
            ministry = "Ministry of Skill Development & Entrepreneurship",
            benefit = "Free Industry-Aligned Technical Certification + ₹8,000 Stipend",
            eligibility = "Unemployed youth, school/college dropouts (Ages 18-35)",
            deadline = "Ongoing Year-Round Enrollments",
            category = "Skill & Employment"
        ),
        GovernmentScheme(
            id = "scheme-nsp",
            title = "National Scholarship Portal Central Sector Scheme",
            ministry = "Ministry of Education",
            benefit = "₹12,000 to ₹20,000 per annum for Higher Studies",
            eligibility = "College/University students above 80th percentile in Class 12",
            deadline = "Last Date: 30 Nov 2026",
            category = "Education"
        ),
        GovernmentScheme(
            id = "scheme-pminternship",
            title = "Prime Minister's Internship Scheme in Top 500 Companies",
            ministry = "Ministry of Corporate Affairs",
            benefit = "₹5,000/month stipend + ₹6,000 one-time contingency grant",
            eligibility = "Graduates/Diploma holders aged 21-24 not in full-time jobs",
            deadline = "Phase-1 Applications Open",
            category = "Career Training"
        )
    )

    val toolsList: List<Tool> = listOf(
        Tool(
            id = "tool-job-photo-signature",
            title = "Government Job Photo & Signature Maker",
            description = "Auto-resize, crop & format photos with date/name stamps as per SSC, UPSC & Railway specs.",
            route = "tools/job-photo-signature",
            iconName = "Badge",
            category = "Photo & Signature",
            badgeText = "Most Popular",
            partNotice = "Engine arriving in Part 3"
        ),
        Tool(
            id = "tool-photo-editor",
            title = "Photo Editor",
            description = "Crop, adjust brightness, enhance contrast, and convert image formats quickly.",
            route = "tools/photo-editor",
            iconName = "Image",
            category = "Image Utility",
            badgeText = "Essential",
            partNotice = "Engine arriving in Part 2"
        ),
        Tool(
            id = "tool-pdf-tools",
            title = "PDF Tools",
            description = "Merge documents, split pages, extract certificates, and manage job application PDFs.",
            route = "tools/pdf",
            iconName = "PictureAsPdf",
            category = "Document",
            partNotice = "Engine arriving in Part 4"
        ),
        Tool(
            id = "tool-image-to-pdf",
            title = "Image to PDF",
            description = "Combine marksheets, photo IDs, and certificates into a single consolidated PDF file.",
            route = "tools/pdf",
            iconName = "Collections",
            category = "Document",
            partNotice = "Engine arriving in Part 4"
        ),
        Tool(
            id = "tool-pdf-compressor",
            title = "PDF Compressor",
            description = "Compress heavy PDF marksheets to under 200KB / 500KB without losing readability.",
            route = "tools/pdf",
            iconName = "Compress",
            category = "Document",
            partNotice = "Engine arriving in Part 4"
        ),
        Tool(
            id = "tool-qr-code",
            title = "QR Code Generator",
            description = "Generate instant QR codes for application links, roll numbers, and contact information.",
            route = "tools/qr",
            iconName = "QrCode",
            category = "Utility",
            partNotice = "Engine arriving in Part 4"
        ),
        Tool(
            id = "tool-calculator",
            title = "Calculator",
            description = "Exact age eligibility calculator as on cutoff date, exam percentage, and cutoff estimator.",
            route = "tools/calculator",
            iconName = "Calculate",
            category = "Math & Eligibility",
            partNotice = "Engine arriving in Part 4"
        ),
        Tool(
            id = "tool-unit-converter",
            title = "Unit Converter",
            description = "Convert image resolutions (pixels to cm/inches at 200/300 DPI), file sizes (KB to MB).",
            route = "tools/unit-converter",
            iconName = "SwapHoriz",
            category = "Conversion",
            partNotice = "Engine arriving in Part 4"
        )
    )
}
