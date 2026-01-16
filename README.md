# 🎓 College Placement Tracker v2.0

A comprehensive Android application for managing college placement activities with role-based access
for Students, HODs, and Training & Placement Officers (TPO).

## ✨ New Features in v2.0

### 🔐 Enhanced Security

- **Password Hashing**: Implemented PBKDF2 with salt for secure password storage
- **Input Validation**: Real-time validation for all user inputs
- **Login Attempt Limiting**: Maximum 5 login attempts to prevent brute force attacks
- **Session Management**: Secure session handling with automatic timeout

### 🔍 Advanced Search & Filter

- **Smart Search**: Search companies by name, role, or location
- **Multi-Filter System**:
    - Filter by package range (0-5 LPA, 5-10 LPA, 10+ LPA)
    - Filter by company type (Service/Product)
    - Filter by eligibility
- **Sort Options**: Sort by package amount or application deadline
- **Real-time Filtering**: Instant results as you type

### 🎨 Improved User Experience

- **Material Design 3**: Modern UI with smooth animations
- **Better Error Handling**: User-friendly error messages with retry options
- **Loading Indicators**: Clear feedback during operations
- **Success Notifications**: Visual confirmation for all actions
- **Smooth Transitions**: Polished animations between screens

### 📱 Smart Notifications

- **Application Status Updates**: Get notified when application status changes
- **New Company Alerts**: Instant notifications for newly posted companies
- **Deadline Reminders**: Reminders for upcoming application deadlines
- **Approval Notifications**: HODs get notified about pending approvals

### 📊 Enhanced Dashboard

- **Real-time Statistics**: Live updates of placement metrics
- **Visual Indicators**: Color-coded status for easy identification
- **Quick Actions**: One-tap access to common actions
- **Deadline Tracking**: Visual countdown for application deadlines

### 🎯 Smart Application System

- **Eligibility Checking**: Automatic validation of CGPA and branch requirements
- **Duplicate Prevention**: Can't apply to the same company twice
- **Position Tracking**: Real-time tracking of available positions
- **Status History**: Complete application timeline

### 📤 Sharing Features

- **Company Sharing**: Share job opportunities via any app
- **Easy Export**: Export company details and share with friends
- **Professional Format**: Well-formatted shareable content

### 🛠️ Developer Features

- **Clean Architecture**: Separated utility classes for better code organization
- **Kotlin Coroutines**: Efficient asynchronous operations
- **LiveData**: Reactive UI updates
- **Room Database**: Robust local data persistence
- **MVVM Pattern**: Maintainable and testable code

## 📱 User Roles & Features

### 👨‍🎓 Student Features

- ✅ View all available companies
- ✅ Filter and search companies
- ✅ Apply to eligible companies
- ✅ Track application status
- ✅ View placement statistics
- ✅ Manage profile
- ✅ Receive notifications
- ✅ Share opportunities

### 👨‍💼 HOD Features

- ✅ View department-wise statistics
- ✅ Approve/reject student applications
- ✅ Monitor department placements
- ✅ View student details
- ✅ Generate reports
- ✅ Track company visits
- ✅ Department analytics

### 👔 TPO Features

- ✅ Add new companies
- ✅ Manage all applications
- ✅ View overall statistics
- ✅ Student management
- ✅ Company management
- ✅ Generate placement reports
- ✅ Monitor all departments
- ✅ Final approval authority

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9.0+

### Installation

1. Clone the repository

```bash
git clone https://github.com/yourusername/college-placement-tracker.git
```

2. Open in Android Studio

3. Sync Gradle dependencies

4. Run the app

### Default Test Accounts

**Student Account:**

- Email: student@college.edu
- Password: student123

**HOD Account:**

- Email: hod.cs@college.edu
- Password: hod123

**TPO Account:**

- Email: tpo@college.edu
- Password: tpo123

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/collegeplacementtracker/
│   │   ├── ui/                    # UI components (Bottom Sheets, Dialogs)
│   │   ├── utils/                 # Utility classes
│   │   │   ├── SecurityUtils.kt   # Password hashing & security
│   │   │   ├── ValidationUtils.kt # Input validation
│   │   │   ├── UIHelper.kt        # UI helper methods
│   │   │   ├── DateUtils.kt       # Date formatting
│   │   │   └── NotificationHelper.kt # Notification management
│   │   ├── *Activity.kt           # Activity classes
│   │   ├── *Adapter.kt            # RecyclerView adapters
│   │   ├── *Dao.kt                # Database DAOs
│   │   └── *.kt                   # Data models
│   └── res/
│       ├── layout/                # XML layouts
│       ├── menu/                  # Menu resources
│       ├── drawable/              # Images and icons
│       └── values/                # Strings, colors, themes
```

## 📦 Dependencies

### Core Libraries

- **AndroidX Core KTX**: 1.12.0
- **Material Components**: 1.11.0
- **ConstraintLayout**: 2.1.4

### Architecture Components

- **Lifecycle**: 2.7.0
- **Room**: 2.6.1
- **WorkManager**: 2.9.0

### Asynchronous Processing

- **Kotlin Coroutines**: 1.7.3

### UI Components

- **RecyclerView**: 1.3.2
- **CardView**: 1.0.0
- **SwipeRefreshLayout**: 1.1.0
- **ViewPager2**: 1.0.0

### Additional Features

- **Lottie**: 6.2.0 (Animations)
- **Coil**: 2.5.0 (Image Loading)
- **MPAndroidChart**: 3.1.0 (Charts & Analytics)
- **iTextG**: 5.5.10 (PDF Generation)
- **Gson**: 2.10.1 (JSON Processing)

## 🎨 UI/UX Improvements

### Color Scheme

- Primary: #667eea (Modern Purple)
- Secondary: #764ba2 (Deep Purple)
- Success: #4CAF50 (Green)
- Warning: #FF9800 (Orange)
- Error: #F44336 (Red)

### Design Principles

- Material Design 3 guidelines
- Smooth animations and transitions
- Consistent spacing and typography
- Accessible color contrasts
- Touch-friendly UI elements (48dp minimum)

## 🔧 Code Quality Features

### Security Best Practices

```kotlin
// Password hashing with PBKDF2
val (hash, salt) = SecurityUtils.hashPassword(password)

// Verification
val isValid = SecurityUtils.verifyPassword(inputPassword, storedHash, salt)
```

### Input Validation

```kotlin
// Email validation
val (isValid, message) = ValidationUtils.isValidEmail(email)

// Password strength check
val (isStrong, message) = ValidationUtils.isStrongPassword(password)
```

### Error Handling

```kotlin
// User-friendly error display
UIHelper.showError(context, "Operation failed")

// Success feedback
UIHelper.showSuccess(context, "Saved successfully!")

// With retry option
UIHelper.showErrorWithRetry(context, "Network error") {
    retryOperation()
}
```

### Notifications

```kotlin
// Application status update
NotificationHelper.sendApplicationStatusNotification(
    context, companyName, status, applicationId
)

// New company notification
NotificationHelper.sendNewCompanyNotification(
    context, companyName, packageAmount, companyId
)
```

## 📊 Database Schema

### User Table

- id, email, password, fullName, phone, role
- rollNumber, branch, cgpa (for students)
- skills, internships, projects
- isActive, createdAt, lastLogin

### Company Table

- id, companyName, jobRole, packageAmount
- location, eligibleBranches, minimumCGPA
- selectionProcess, numberOfRounds
- applicationDeadline, totalPositions
- isActive, postedBy, companyType

### Application Table

- id, studentId, companyId, status
- currentRound, appliedAt, lastUpdated
- offeredPackage, selectedDate
- hodApproved, tpoApproved

## 🧪 Testing

### Unit Tests

- ViewModel tests
- Utility function tests
- Validation tests

### Instrumentation Tests

- Database tests
- UI tests
- Navigation tests

Run tests:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 🔄 Future Enhancements

### Planned Features

- [ ] Resume upload and parsing
- [ ] Interview scheduling
- [ ] Video interview integration
- [ ] AI-powered resume analysis
- [ ] Salary negotiation calculator
- [ ] Alumni networking
- [ ] Company reviews and ratings
- [ ] Mock interview practice
- [ ] Placement preparation resources
- [ ] Analytics dashboard with charts
- [ ] Export to Excel/PDF
- [ ] Email integration
- [ ] Calendar sync
- [ ] Dark mode
- [ ] Multi-language support

## 📝 Version History

### Version 2.0 (Current)

- ✅ Enhanced security with password hashing
- ✅ Advanced search and filter system
- ✅ Smart notifications
- ✅ Improved UI/UX
- ✅ Better error handling
- ✅ Code refactoring with utils package
- ✅ Input validation
- ✅ Sharing features
- ✅ Deadline tracking

### Version 1.0

- Basic CRUD operations
- Role-based access
- Application management
- Simple dashboard
- Database integration

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Write unit tests for new features
- Update documentation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - Initial work and v2.0 enhancements

## 🙏 Acknowledgments

- Material Design team for UI guidelines
- Android Jetpack team for architecture components
- Open source community for amazing libraries

## 📞 Support

For support, email support@example.com or create an issue in the repository.

## 🌟 Show Your Support

Give a ⭐️ if this project helped you!

---

**Built with ❤️ using Kotlin and Android Jetpack**
