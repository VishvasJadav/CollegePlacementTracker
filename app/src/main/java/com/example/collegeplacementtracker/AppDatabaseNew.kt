package com.example.collegeplacementtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Student::class,
        User::class,
        Company::class,
        Application::class,
        Interview::class,
        Resume::class,
        Document::class,
        Notification::class,
        Alumni::class
    ],
    version = 7,  // V3.0 - Added Resume, Document, Notification, Alumni
    exportSchema = false
)
abstract class AppDatabaseNew : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun userDao(): UserDao
    abstract fun companyDao(): CompanyDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun interviewDao(): InterviewDao
    abstract fun resumeDao(): ResumeDao
    abstract fun documentDao(): DocumentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun alumniDao(): AlumniDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseNew? = null

        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user_table ADD COLUMN professionalSummary TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE user_table ADD COLUMN certifications TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE user_table ADD COLUMN linkedinUrl TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE user_table ADD COLUMN resumeUrl TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE company_table ADD COLUMN employeesCount INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE company_table ADD COLUMN workFromHomePolicy INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE company_table ADD COLUMN learningOpportunities INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE company_table ADD COLUMN growthPotential INTEGER DEFAULT NULL")
            }
        }

        val MIGRATION_5_6 = object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add indices to application_table for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_application_table_studentId ON application_table(studentId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_application_table_companyId ON application_table(companyId)")
            }
        }

        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create resumes table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS resumes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        studentId INTEGER NOT NULL,
                        fileName TEXT NOT NULL,
                        filePath TEXT NOT NULL,
                        fileSize INTEGER NOT NULL,
                        mimeType TEXT NOT NULL,
                        resumeType TEXT NOT NULL,
                        uploadedAt INTEGER NOT NULL,
                        lastModified INTEGER NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        parsedSkills TEXT,
                        parsedExperience TEXT,
                        parsedEducation TEXT,
                        resumeScore INTEGER,
                        lastScanned INTEGER,
                        FOREIGN KEY(studentId) REFERENCES user_table(id) ON DELETE CASCADE
                    )
                """
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_resumes_studentId ON resumes(studentId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_resumes_uploadedAt ON resumes(uploadedAt)")

                // Create documents table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS documents (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        studentId INTEGER NOT NULL,
                        documentType TEXT NOT NULL,
                        fileName TEXT NOT NULL,
                        filePath TEXT NOT NULL,
                        uploadedAt INTEGER NOT NULL,
                        verificationStatus TEXT NOT NULL DEFAULT 'pending',
                        verifiedBy INTEGER,
                        verifiedAt INTEGER,
                        rejectionReason TEXT,
                        expiryDate INTEGER,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(studentId) REFERENCES user_table(id) ON DELETE CASCADE
                    )
                """
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_documents_studentId ON documents(studentId)")

                // Create notifications table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS notifications (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        type TEXT NOT NULL,
                        relatedId INTEGER,
                        createdAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL DEFAULT 0,
                        readAt INTEGER,
                        actionUrl TEXT,
                        priority TEXT NOT NULL DEFAULT 'normal'
                    )
                """
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_userId ON notifications(userId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_createdAt ON notifications(createdAt)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_notifications_isRead ON notifications(isRead)")

                // Create alumni table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS alumni (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        studentId INTEGER NOT NULL,
                        graduationYear INTEGER NOT NULL,
                        currentCompany TEXT NOT NULL,
                        currentPosition TEXT NOT NULL,
                        currentPackage REAL,
                        yearsOfExperience INTEGER NOT NULL DEFAULT 0,
                        linkedInUrl TEXT,
                        githubUrl TEXT,
                        portfolioUrl TEXT,
                        willingToMentor INTEGER NOT NULL DEFAULT 0,
                        mentorshipAreas TEXT,
                        availableForReferrals INTEGER NOT NULL DEFAULT 0,
                        bio TEXT,
                        achievements TEXT,
                        isVerified INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        lastUpdated INTEGER NOT NULL,
                        FOREIGN KEY(studentId) REFERENCES user_table(id) ON DELETE CASCADE
                    )
                """
                )
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_alumni_studentId ON alumni(studentId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_alumni_graduationYear ON alumni(graduationYear)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_alumni_currentCompany ON alumni(currentCompany)")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabaseNew {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseNew::class.java,
                    "placement_database_v2"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration()  // For development: recreate DB if migration fails
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Clear database instance (useful for logout or data reset)
        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.userDao(),
                        database.companyDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao, companyDao: CompanyDao) {
            // Create default TPO account
            val tpoUser = User(
                email = "tpo@college.edu",
                password = "tpo123",
                fullName = "Training Placement Officer",
                phone = "9876543210",
                role = UserRole.TPO,
                branch = "All"
            )
            userDao.insert(tpoUser)

            // Create default HOD accounts
            val hodCS = User(
                email = "hod.cs@college.edu",
                password = "hod123",
                fullName = "HOD Computer Science",
                phone = "9876543211",
                role = UserRole.HOD,
                branch = "Computer Science"
            )
            userDao.insert(hodCS)

            val hodIT = User(
                email = "hod.it@college.edu",
                password = "hod123",
                fullName = "HOD Information Technology",
                phone = "9876543212",
                role = UserRole.HOD,
                branch = "Information Technology"
            )
            userDao.insert(hodIT)

            // Create sample student
            val student = User(
                email = "student@college.edu",
                password = "student123",
                fullName = "Sample Student",
                phone = "9876543213",
                role = UserRole.STUDENT,
                rollNumber = "CS001",
                branch = "Computer Science",
                cgpa = 8.5,
                professionalSummary = "Passionate computer science student with experience in mobile development",
                skills = "Java, Kotlin, Android",
                internships = "Summer Intern at Tech Corp",
                projects = "Mobile App Development",
                certifications = "Oracle Java Certification",
                linkedinUrl = "https://linkedin.com/in/samplestudent",
                resumeUrl = null
            )
            userDao.insert(student)

            // Create sample companies with professional insights
            val company1 = Company(
                companyName = "Google",
                companyLogo = null,
                jobRole = "Software Engineer",
                jobDescription = "Develop and maintain software applications",
                packageAmount = 16.0,
                location = "Bangalore",
                eligibleBranches = "Computer Science, Information Technology, Electronics",
                minimumCGPA = 7.0,
                backlogs = 0,
                selectionProcess = "Online Test, Technical Interview, HR Interview",
                numberOfRounds = 3,
                applicationDeadline = "2024-02-15",
                driveDate = "2024-03-01",
                isActive = true,
                totalPositions = 10,
                postedBy = 1,
                websiteUrl = "https://careers.google.com",
                companyType = "Product",
                bond = "2 years",
                employeesCount = 150000,
                workFromHomePolicy = true,
                learningOpportunities = true,
                growthPotential = true
            )

            val company2 = Company(
                companyName = "Microsoft",
                companyLogo = null,
                jobRole = "Software Development Engineer",
                jobDescription = "Design, develop and test software solutions",
                packageAmount = 14.5,
                location = "Hyderabad",
                eligibleBranches = "Computer Science, Information Technology, Electronics",
                minimumCGPA = 7.5,
                backlogs = 0,
                selectionProcess = "Coding Round, Technical Rounds, HR Round",
                numberOfRounds = 4,
                applicationDeadline = "2024-02-20",
                driveDate = "2024-03-10",
                isActive = true,
                totalPositions = 15,
                postedBy = 1,
                websiteUrl = "https://careers.microsoft.com",
                companyType = "Product",
                bond = "2 years",
                employeesCount = 220000,
                workFromHomePolicy = true,
                learningOpportunities = true,
                growthPotential = true
            )

            val company3 = Company(
                companyName = "TCS",
                companyLogo = null,
                jobRole = "Assistant System Engineer",
                jobDescription = "Maintain and support client systems",
                packageAmount = 3.5,
                location = "Mumbai",
                eligibleBranches = "All Engineering Branches",
                minimumCGPA = 6.0,
                backlogs = 1,
                selectionProcess = "Aptitude, Technical, HR",
                numberOfRounds = 3,
                applicationDeadline = "2024-03-01",
                driveDate = "2024-03-15",
                isActive = true,
                totalPositions = 50,
                postedBy = 1,
                websiteUrl = "https://www.tcs.com",
                companyType = "Service",
                bond = "2 years",
                employeesCount = 500000,
                workFromHomePolicy = false,
                learningOpportunities = true,
                growthPotential = false
            )

            companyDao.insert(company1)
            companyDao.insert(company2)
            companyDao.insert(company3)
        }
    }
}
