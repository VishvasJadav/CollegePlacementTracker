# 🚀 Quick Start Guide

## College Placement Tracker v2.0

### Welcome! This guide will help you get started in 5 minutes.

---

## 📱 For Users

### First Time Setup

1. **Install the App**
    - Download from Play Store / APK
    - Grant notification permissions
    - Allow storage access (for resume upload in future)

2. **Login with Test Accounts**

   **Student Account:**
   ```
   Email: student@college.edu
   Password: student123
   ```

   **HOD Account:**
   ```
   Email: hod.cs@college.edu
   Password: hod123
   ```

   **TPO Account:**
   ```
   Email: tpo@college.edu
   Password: tpo123
   ```

3. **Explore Features**
    - Browse companies
    - Use search and filters
    - Apply to eligible companies
    - Check your applications

---

## 👨‍🎓 Student Guide

### How to Find Companies

1. **Browse All Companies**
    - Tap "Companies" in bottom navigation
    - Scroll through available opportunities

2. **Search Companies**
    - Use search bar at top
    - Search by:
        - Company name (e.g., "Google")
        - Job role (e.g., "Software Engineer")
        - Location (e.g., "Bangalore")

3. **Filter Companies**
    - Tap filter chips below search
    - Options:
        - Package Range: 0-5 LPA, 5-10 LPA, 10+ LPA
        - Type: Service, Product
    - Multiple filters can be combined

4. **Sort Results**
    - Menu (⋮) → Sort Options
    - By Package (highest first)
    - By Deadline (most urgent first)

### How to Apply

1. **View Company Details**
    - Tap on any company card
    - Read full job description
    - Check eligibility criteria
    - Note the deadline

2. **Apply**
    - Tap "Apply Now" button
    - System checks eligibility automatically
    - Confirmation notification appears
    - Track in "My Applications"

3. **Track Applications**
    - Tap "Applications" in bottom navigation
    - See all your applications
    - Status: Pending, Shortlisted, Selected, Rejected
    - Tap for detailed view

### Getting Notifications

**You'll be notified for:**

- ✅ New companies posted
- ✅ Application status changes
- ✅ Upcoming deadlines (3 days before)
- ✅ Interview schedules (future)

**Managing Notifications:**

- Settings → Apps → Placement Tracker → Notifications
- Customize sound, vibration, priority

### Understanding Status Badges

```
⏳ PENDING     - Application under review
🎉 SHORTLISTED - You're in the next round!
🎊 SELECTED    - Congratulations! You're hired!
❌ REJECTED    - Better luck next time
```

### Tips for Success

✅ **DO:**

- Update your profile regularly
- Apply early (deadlines matter!)
- Check eligibility before applying
- Keep notifications ON
- Share opportunities with friends

❌ **DON'T:**

- Apply to ineligible companies (system will reject)
- Miss deadlines (system won't allow late applications)
- Apply twice to same company (system prevents this)

---

## 👨‍💼 HOD Guide

### Managing Your Department

1. **View Department Statistics**
    - Dashboard shows:
        - Total students
        - Eligible students
        - Placed students
        - Placement percentage

2. **Approve Applications**
    - Tap "Pending Approvals" card
    - View student details
    - Approve or Reject with reason
    - Student gets notified

3. **Monitor Students**
    - View all department students
    - Check placement status
    - Track applications
    - Generate reports

### Key Features

- **Real-time Updates**: Statistics update automatically
- **Filters**: View placed/not placed students
- **Export**: Download department reports (coming soon)
- **Analytics**: Charts and graphs (coming soon)

---

## 👔 TPO Guide

### System Administration

1. **Add New Companies**
    - Tap + (FAB) button
    - Fill company details:
        - Name, role, package
        - Location, type
        - Eligibility criteria
        - Selection process
        - Deadline
    - Tap "Add Company"
    - All eligible students notified

2. **Manage Applications**
    - View all applications
    - Filter by status/department
    - Final approval authority
    - Bulk operations (coming soon)

3. **Student Management**
    - Add/Edit student details
    - Manage eligibility
    - Track placements
    - Generate reports

4. **Company Management**
    - Edit company details
    - Activate/Deactivate
    - Track applications per company
    - Position management

### Dashboard Overview

**Key Metrics:**

- Total Students
- Placed Students
- Placement Percentage
- Average Package
- Highest Package
- Active Companies
- Pending Approvals

---

## 💻 For Developers

### Setup Development Environment

1. **Prerequisites**
   ```bash
   - Android Studio Hedgehog (2023.1.1) or later
   - JDK 17
   - Android SDK 24+
   - Kotlin 1.9.0+
   ```

2. **Clone Repository**
   ```bash
   git clone https://github.com/yourusername/college-placement-tracker.git
   cd college-placement-tracker
   ```

3. **Open in Android Studio**
    - File → Open → Select project folder
    - Wait for Gradle sync
    - Trust the project

4. **Run the App**
    - Click Run (▶️) button
    - Select emulator or device
    - Wait for build

### Project Structure Overview

```
app/src/main/
├── java/com/example/collegeplacementtracker/
│   ├── ui/                     # UI components
│   ├── utils/                  # Utility classes
│   ├── *Activity.kt            # Activities
│   ├── *Adapter.kt             # RecyclerView adapters
│   ├── *Dao.kt                 # Database DAOs
│   ├── *.kt                    # Models
│   └── AppDatabaseNew.kt       # Database
│
└── res/
    ├── layout/                 # XML layouts
    ├── menu/                   # Menus
    ├── drawable/               # Images
    └── values/                 # Strings, colors
```

### Key Classes

1. **Utilities** (`utils/`)
    - `SecurityUtils` - Password hashing
    - `ValidationUtils` - Input validation
    - `UIHelper` - UI feedback
    - `DateUtils` - Date operations
    - `NotificationHelper` - Notifications

2. **Activities**
    - `LoginActivity` - Authentication
    - `StudentDashboardActivityEnhanced` - Student home
    - `HODDashboardActivityEnhanced` - HOD home
    - `TPODashboardActivityEnhanced` - TPO home
    - `CompanyListActivity` - Browse companies

3. **Database** (`AppDatabaseNew.kt`)
    - Tables: User, Company, Application, Student
    - DAOs: UserDao, CompanyDao, ApplicationDao, StudentDao

### Adding New Features

1. **Add New Utility Function**
   ```kotlin
   // In utils/YourUtils.kt
   object YourUtils {
       fun yourFunction(): Result {
           // Implementation
       }
   }
   ```

2. **Add New Activity**
   ```kotlin
   class YourActivity : AppCompatActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_your)
           // Setup
       }
   }
   ```

3. **Add Database Table**
   ```kotlin
   @Entity(tableName = "your_table")
   data class YourModel(
       @PrimaryKey(autoGenerate = true)
       val id: Int = 0,
       // fields
   )
   
   @Dao
   interface YourDao {
       @Query("SELECT * FROM your_table")
       fun getAll(): LiveData<List<YourModel>>
   }
   ```

### Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

### Building Release APK

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# APK location
app/build/outputs/apk/
```

---

## 🐛 Troubleshooting

### Common Issues

**1. App Won't Start**

```
Solution: Clean and rebuild
- Build → Clean Project
- Build → Rebuild Project
```

**2. Database Errors**

```
Solution: Clear app data
- Settings → Apps → Placement Tracker → Clear Data
OR
- Increment database version in AppDatabaseNew.kt
```

**3. Gradle Sync Failed**

```
Solution: Invalidate caches
- File → Invalidate Caches → Restart
```

**4. Can't Login**

```
Solution: Use test accounts (see above)
OR check if database populated (see AppDatabaseNew.kt)
```

**5. Search Not Working**

```
Solution: 
- Check if companies exist in database
- Verify SearchView is initialized
- Check filter chips state
```

### Getting Help

- 📧 Email: support@example.com
- 🐛 Issues: GitHub Issues
- 💬 Discussion: GitHub Discussions
- 📚 Docs: README.md

---

## 📊 Feature Checklist

### Current Features (v2.0)

✅ **Authentication**

- [x] Login with email/password
- [x] Signup for new students
- [x] Role-based access (Student/HOD/TPO)
- [x] Session management
- [x] Password hashing (PBKDF2)

✅ **Company Management**

- [x] View all companies
- [x] Search companies
- [x] Filter by package/type
- [x] Sort by various criteria
- [x] View detailed information
- [x] Share company details

✅ **Application System**

- [x] Apply to companies
- [x] Track applications
- [x] View status updates
- [x] Eligibility checking
- [x] Duplicate prevention
- [x] Deadline validation

✅ **Notifications**

- [x] Application updates
- [x] New companies
- [x] Deadline reminders
- [x] Approval requests

✅ **Dashboard**

- [x] Statistics overview
- [x] Quick actions
- [x] Status indicators
- [x] Real-time updates

### Upcoming Features (v2.1)

⏳ **Coming Soon**

- [ ] Resume upload
- [ ] PDF reports
- [ ] Charts & analytics
- [ ] Dark mode
- [ ] Interview scheduling
- [ ] Calendar sync
- [ ] Email notifications
- [ ] Bulk operations

---

## 🎯 Usage Tips

### For Best Experience

1. **Keep App Updated**
    - Check for updates regularly
    - Enable auto-update in Play Store

2. **Enable Notifications**
    - Don't miss important updates
    - Configure in app settings

3. **Complete Your Profile**
    - Add skills, projects
    - Upload resume (coming soon)
    - Keep CGPA updated

4. **Use Filters Wisely**
    - Combine filters for precise results
    - Save time with smart filtering

5. **Apply Early**
    - Early bird gets the worm
    - Don't wait for deadline

### Keyboard Shortcuts (Future)

```
Ctrl + S: Search
Ctrl + F: Filter
Ctrl + R: Refresh
Ctrl + N: New (TPO only)
```

---

## 📈 Performance Tips

### For Smooth Experience

1. **Clear Cache** (Monthly)
    - Settings → Storage → Clear Cache

2. **Free Up Space**
    - Keep at least 500MB free
    - Delete old documents

3. **Update Android**
    - Keep OS updated
    - Better performance & security

4. **Restart Regularly**
    - Restart app if slow
    - Restart phone weekly

---

## 🎓 Learning Resources

### For Students

- 📱 App Tutorial: First launch walkthrough
- 📖 User Guide: Full documentation
- 🎥 Video Tutorials: YouTube channel (coming)
- 💬 FAQs: In-app help section

### For Developers

- 📚 Code Documentation: KDoc comments
- 🏗️ Architecture Guide: README.md
- 🔧 API Documentation: JavaDoc/KDoc
- 🧪 Testing Guide: Test files

---

## 🌟 Pro Tips

### Maximize Your Success

**Students:**

1. Check app daily for new companies
2. Set deadline reminders (3 days before)
3. Prepare documents in advance
4. Network with placed seniors
5. Use search for target companies

**HODs:**

1. Review approvals daily
2. Monitor department statistics
3. Guide struggling students
4. Coordinate with TPO
5. Generate regular reports

**TPOs:**

1. Post companies immediately
2. Send bulk notifications
3. Track system usage
4. Coordinate with departments
5. Maintain company relationships

---

## 🎉 You're Ready!

Congratulations! You now know everything to get started with the College Placement Tracker.

**Quick Links:**

- 📖 Full Documentation: README.md
- 📝 Change Log: CHANGELOG.md
- 🎯 Implementation Details: IMPLEMENTATION_SUMMARY.md

**Need Help?**

- Create an issue on GitHub
- Email support team
- Check in-app help

**Happy Placement Hunting! 🚀**

---

*Last Updated: January 2, 2025*
*Version: 2.0.0*
