# Contributing to SumUp

First off, thank you for considering contributing to SumUp! 🎉 We're excited to have you join our community.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Process](#development-process)
- [Style Guidelines](#style-guidelines)
- [Testing](#testing)
- [Documentation](#documentation)
- [Community](#community)

## 📜 Code of Conduct

This project and everyone participating in it is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to ductam7415@gmail.com.

## 🚀 Getting Started

### Prerequisites

- Android Studio Koala (2024.1.1) or later
- JDK 17
- Git
- GitHub account
- Basic knowledge of Kotlin and Android development

### Setting Up Your Development Environment

1. **Fork the repository**
   ```bash
   # Navigate to https://github.com/Ductam7415vn/SumUp
   # Click the "Fork" button
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SumUp.git
   cd SumUp
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/Ductam7415vn/SumUp.git
   ```

4. **Set up API key**
   ```bash
   # Copy the template
   cp local.properties.template local.properties
   
   # Add your Gemini API key
   echo "GEMINI_API_KEY=your_api_key_here" >> local.properties
   ```

5. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory
   - Let Gradle sync complete

## 🤝 How Can I Contribute?

### 🐛 Reporting Bugs

Before creating bug reports, please check [existing issues](https://github.com/Ductam7415vn/SumUp/issues) to avoid duplicates.

**When reporting a bug, include:**

- **Clear and descriptive title**
- **Detailed description** of the issue
- **Steps to reproduce** the behavior
- **Expected behavior** description
- **Screenshots or screen recordings** if applicable
- **Environment details:**
  ```markdown
  - Device: [e.g. Pixel 6]
  - OS: [e.g. Android 13]
  - App Version: [e.g. 1.0.3]
  - Kotlin Version: [e.g. 2.0.21]
  ```

### 💡 Suggesting Enhancements

Enhancement suggestions are welcome! Please create an issue with:

- **Use case:** Why is this enhancement needed?
- **Proposed solution:** How do you envision it working?
- **Alternatives considered:** What other solutions did you think about?
- **Additional context:** Mockups, examples, etc.

### 📝 Your First Code Contribution

Unsure where to begin? Look for these labels:

- `good first issue` - Simple issues perfect for beginners
- `help wanted` - Issues where we need community help
- `documentation` - Documentation improvements

### 🔄 Pull Requests

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow our [coding standards](#coding-standards)
   - Add tests for new functionality
   - Update documentation as needed

3. **Commit your changes**
   ```bash
   git commit -m "feat: add amazing new feature"
   ```

4. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Open a Pull Request**
   - Use our PR template
   - Link related issues
   - Ensure all checks pass

## 💻 Development Process

### Branch Strategy

- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - New features
- `fix/*` - Bug fixes
- `docs/*` - Documentation updates

### Workflow

1. Always branch from `develop`
2. Keep commits atomic and meaningful
3. Write descriptive commit messages
4. Keep PRs focused and small
5. Request reviews from maintainers

## 📐 Style Guidelines

### Kotlin Code Style

We follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) with these additions:

```kotlin
// ✅ Good
class SummaryRepository @Inject constructor(
    private val apiService: GeminiApiService,
    private val dao: SummaryDao
) {
    suspend fun summarizeText(text: String): Result<Summary> {
        return try {
            val response = apiService.summarize(text)
            Result.Success(response.toDomainModel())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// ❌ Bad
class summary_repository @Inject constructor(val api: GeminiApiService, val d: SummaryDao) {
    suspend fun doSummary(t: String) = try {
        Result.Success(api.summarize(t).toDomainModel())
    } catch (e: Exception) { Result.Error(e) }
}
```

### Compose UI Guidelines

- Use `Modifier` parameter for all composables
- Follow Material 3 design guidelines
- Provide content descriptions for accessibility
- Keep composables small and focused

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Test additions or changes
- `build`: Build system changes
- `ci`: CI configuration changes
- `chore`: Other changes

**Examples:**
```bash
feat(summary): add PDF export functionality
fix(ocr): resolve camera permission crash on Android 14
docs(readme): update installation instructions
```

## 🧪 Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests with coverage
./gradlew testDebugUnitTestCoverage

# Lint checks
./gradlew lint
```

### Writing Tests

- Aim for >70% code coverage
- Test edge cases and error scenarios
- Use meaningful test names
- Follow AAA pattern (Arrange, Act, Assert)

```kotlin
@Test
fun `summarizeText returns error when text is empty`() = runTest {
    // Arrange
    val useCase = SummarizeTextUseCase(repository)
    
    // Act
    val result = useCase("")
    
    // Assert
    assertTrue(result is Result.Error)
    assertEquals("Text cannot be empty", result.message)
}
```

## 📚 Documentation

### Code Documentation

- Add KDoc comments for public APIs
- Include examples for complex functionality
- Document non-obvious implementations

```kotlin
/**
 * Summarizes the provided text using the specified persona.
 *
 * @param text The text to summarize (50-30,000 characters)
 * @param persona The AI persona to use for summarization
 * @return Flow emitting the summarization progress and result
 * @throws IllegalArgumentException if text length is invalid
 *
 * Example:
 * ```
 * summarizeTextUseCase(
 *     text = "Long article text...",
 *     persona = Persona.STUDENT
 * ).collect { result ->
 *     when (result) {
 *         is Result.Success -> updateUI(result.data)
 *         is Result.Error -> showError(result.error)
 *         is Result.Loading -> showProgress()
 *     }
 * }
 * ```
 */
```

### User Documentation

- Update README.md for user-facing changes
- Add screenshots for UI changes
- Update API documentation for backend changes

## 🌟 Recognition

Contributors will be:
- Listed in our [Contributors](https://github.com/Ductam7415vn/SumUp/graphs/contributors) page
- Mentioned in release notes for significant contributions
- Eligible for special contributor badges

## 🤔 Questions?

- 💬 [GitHub Discussions](https://github.com/Ductam7415vn/SumUp/discussions) - General discussions
- 🐛 [GitHub Issues](https://github.com/Ductam7415vn/SumUp/issues) - Bug reports and features
- 📧 Email: ductam7415@gmail.com - Private inquiries

## 📋 Checklist Before Submitting

- [ ] I've read the contributing guidelines
- [ ] I've checked for existing issues/PRs
- [ ] I've followed the code style guidelines
- [ ] I've added tests for my changes
- [ ] I've updated relevant documentation
- [ ] All tests pass locally
- [ ] I've tested on different screen sizes
- [ ] I've signed the CLA (if required)

---

Thank you for making SumUp better! 🚀 Your contributions help thousands of users save time every day.