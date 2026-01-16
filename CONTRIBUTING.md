# 🤝 Contributing to College Placement Tracker

Thank you for your interest in contributing! This document provides guidelines for contributing to
the project.

## 📋 Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Workflow](#development-workflow)
4. [Coding Standards](#coding-standards)
5. [Commit Messages](#commit-messages)
6. [Pull Request Process](#pull-request-process)
7. [Testing Guidelines](#testing-guidelines)
8. [Documentation](#documentation)

---

## 📜 Code of Conduct

### Our Pledge

We pledge to make participation in our project a harassment-free experience for everyone.

### Expected Behavior

- Be respectful and inclusive
- Accept constructive criticism gracefully
- Focus on what is best for the community
- Show empathy towards others

### Unacceptable Behavior

- Harassment of any kind
- Publishing others' private information
- Trolling or insulting comments
- Unprofessional conduct

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Git
- GitHub account

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/college-placement-tracker.git
   cd college-placement-tracker
   ```

3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/college-placement-tracker.git
   ```

4. Create a branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

---

## 💻 Development Workflow

### 1. Sync Your Fork

```bash
git fetch upstream
git checkout main
git merge upstream/main
git push origin main
```

### 2. Create Feature Branch

```bash
git checkout -b feature/amazing-feature
```

**Branch Naming:**

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation
- `refactor/` - Code refactoring
- `test/` - Adding tests
- `style/` - Code style improvements

### 3. Make Changes

- Write clean, readable code
- Follow existing patterns
- Add comments for complex logic
- Update documentation

### 4. Test Your Changes

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### 5. Commit Your Changes

```bash
git add .
git commit -m "feat: add amazing feature"
git push origin feature/amazing-feature
```

### 6. Create Pull Request

- Go to GitHub
- Click "New Pull Request"
- Fill in the template
- Wait for review

---

## 📝 Coding Standards

### Kotlin Style Guide

#### 1. Naming Conventions

```kotlin
// Classes - PascalCase
class StudentAdapter

// Functions - camelCase
fun calculateAverage()

// Variables - camelCase
val studentList = listOf()

// Constants - UPPERCASE_SNAKE_CASE
const val MAX_STUDENTS = 100

// Private properties - leading underscore (optional)
private var _isLoading = false
```

#### 2. File Structure

```kotlin
// 1. Package declaration
package com.example.collegeplacementtracker

// 2. Imports (organized)
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// 3. Class declaration
class YourActivity : AppCompatActivity() {
    
    // 4. Companion object (if needed)
    companion object {
        const val TAG = "YourActivity"
    }
    
    // 5. Properties
    private lateinit var adapter: YourAdapter
    
    // 6. Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    // 7. Public methods
    fun publicMethod() { }
    
    // 8. Private methods
    private fun privateMethod() { }
}
```

#### 3. Code Formatting

```kotlin
// Use 4 spaces for indentation
class Example {
    fun example() {
        if (condition) {
            // code
        }
    }
}

// Line length: Max 120 characters
val longString = "This is a very long string that should be " +
    "split across multiple lines for readability"

// Function parameters
fun longFunction(
    parameter1: String,
    parameter2: Int,
    parameter3: Boolean
) {
    // code
}
```

#### 4. Comments

```kotlin
/**
 * Calculate average CGPA of students
 * 
 * @param students List of students
 * @return Average CGPA or 0.0 if list is empty
 */
fun calculateAverageCGPA(students: List<Student>): Double {
    if (students.isEmpty()) return 0.0
    return students.sumOf { it.cgpa ?: 0.0 } / students.size
}

// Use single-line comments for brief explanations
// This checks if user is eligible

/* Use multi-line comments for
   longer explanations or
   temporarily disabling code */
```

### XML Style Guide

#### Layout Files

```xml
<!-- Use meaningful IDs -->
<TextView
    android:id="@+id/studentNameTextView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/student_name"
    android:textSize="16sp"
    android:textColor="@color/black"
    android:padding="16dp" />

<!-- Group related attributes -->
<!-- 1. ID -->
<!-- 2. Layout dimensions -->
<!-- 3. Layout constraints/positioning -->
<!-- 4. Content/appearance -->
<!-- 5. Other attributes -->
```

#### Naming Conventions

```xml
<!-- Layouts -->
activity_login.xml
fragment_home.xml
item_student.xml
dialog_confirm.xml

<!-- IDs -->
android:id="@+id/loginButton"
android:id="@+id/studentNameTextView"

<!-- Strings -->
<string name="app_name">College Placement Tracker</string>
<string name="error_invalid_email">Invalid email address</string>

<!-- Colors -->
<color name="primary">#667eea</color>
<color name="error_red">#F44336</color>

<!-- Dimensions -->
<dimen name="padding_normal">16dp</dimen>
<dimen name="text_size_large">20sp</dimen>
```

---

## 💬 Commit Messages

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style (formatting, missing semicolons, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples

```bash
# Feature
feat(auth): add password reset functionality

# Bug fix
fix(search): resolve crash on empty query

# Documentation
docs(readme): update installation instructions

# Refactor
refactor(utils): extract validation logic to separate class

# Multiple changes
feat(dashboard): add statistics cards

- Add placement percentage card
- Add average package display
- Implement real-time updates
```

---

## 🔄 Pull Request Process

### Before Creating PR

1. ✅ Code compiles without errors
2. ✅ All tests pass
3. ✅ Code follows style guidelines
4. ✅ Documentation updated
5. ✅ No merge conflicts

### PR Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing completed
- [ ] All tests passing

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings

## Screenshots (if applicable)
Add screenshots here

## Related Issues
Closes #123
```

### Review Process

1. Automated checks run
2. Code review by maintainers
3. Changes requested (if needed)
4. Approval and merge

---

## 🧪 Testing Guidelines

### Unit Tests

```kotlin
class ValidationUtilsTest {
    
    @Test
    fun `isValidEmail returns true for valid email`() {
        val (isValid, _) = ValidationUtils.isValidEmail("test@example.com")
        assertTrue(isValid)
    }
    
    @Test
    fun `isValidEmail returns false for invalid email`() {
        val (isValid, message) = ValidationUtils.isValidEmail("invalid")
        assertFalse(isValid)
        assertTrue(message.isNotEmpty())
    }
}
```

### UI Tests

```kotlin
@Test
fun testLogin() {
    // Type email
    onView(withId(R.id.emailEditText))
        .perform(typeText("test@example.com"))
    
    // Type password
    onView(withId(R.id.passwordEditText))
        .perform(typeText("password123"))
    
    // Click login
    onView(withId(R.id.loginButton))
        .perform(click())
    
    // Verify navigation
    onView(withId(R.id.studentDashboard))
        .check(matches(isDisplayed()))
}
```

### Test Coverage

Aim for:

- Unit tests: 80%+ coverage
- Critical paths: 100% coverage
- UI tests: Major user flows

---

## 📚 Documentation

### Code Documentation

```kotlin
/**
 * Manager class for handling user authentication and session management.
 * 
 * This class provides methods for user login, logout, and session validation.
 * All sensitive data is stored securely using SharedPreferences with encryption.
 * 
 * @property context Application context for accessing SharedPreferences
 */
class SessionManager(private val context: Context) {
    
    /**
     * Saves user session data after successful login.
     * 
     * @param user The authenticated user object
     * @throws SecurityException if encryption fails
     */
    fun saveUserSession(user: User) {
        // Implementation
    }
}
```

### README Updates

When adding features:

1. Update Features section
2. Add usage examples
3. Update screenshots
4. Document breaking changes

### Changelog

Update CHANGELOG.md:

```markdown
## [Unreleased]

### Added
- New feature description

### Changed
- Modified feature description

### Fixed
- Bug fix description
```

---

## 🎯 Contribution Ideas

### Good First Issues

- [ ] Add input validation for new fields
- [ ] Improve error messages
- [ ] Add unit tests
- [ ] Fix typos in documentation
- [ ] Improve accessibility

### Medium Complexity

- [ ] Implement dark mode
- [ ] Add new chart types
- [ ] Improve search algorithm
- [ ] Add export to Excel
- [ ] Implement caching

### Advanced

- [ ] Add AI resume analysis
- [ ] Implement video interviews
- [ ] Build recommendation system
- [ ] Add machine learning features
- [ ] Optimize database queries

---

## 🏆 Recognition

### Contributors

All contributors will be:

- Listed in CONTRIBUTORS.md
- Mentioned in release notes
- Given credit in documentation

### Hall of Fame

Top contributors get:

- Special mention in README
- Contributor badge
- Priority support for their PRs

---

## 📞 Getting Help

### Communication Channels

- 💬 GitHub Discussions: General questions
- 🐛 GitHub Issues: Bug reports, feature requests
- 📧 Email: maintainer@example.com
- 💻 Discord: [Join our server]

### Response Time

- Issues: 24-48 hours
- Pull requests: 2-7 days
- Emails: 48 hours

---

## 📜 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

## 🙏 Thank You!

Your contributions make this project better. We appreciate your time and effort!

**Happy Coding! 🚀**

---

*Last Updated: January 2, 2025*
