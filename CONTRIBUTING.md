# Contributing to SumUp

First off, thank you for considering contributing to SumUp! 🎉

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected behavior**
- **Actual behavior**
- **Screenshots** (if applicable)
- **Device/OS information**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear title and description**
- **Use case** - why is this needed?
- **Possible implementation**
- **Alternative solutions**

### Pull Requests

1. Fork the repo and create your branch from `develop`
2. Follow the existing code style
3. Add tests if applicable
4. Ensure the test suite passes
5. Update documentation
6. Issue that pull request!

## Development Setup

```bash
# Clone your fork
git clone https://github.com/your-username/SumUp.git

# Add upstream remote
git remote add upstream https://github.com/Ductam7415vn/SumUp.git

# Create feature branch
git checkout -b feature/amazing-feature develop
```

## Coding Standards

### Kotlin Style Guide

- Follow [Kotlin official conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable names
- Keep functions small and focused
- Document complex logic

### Commit Messages

Follow conventional commits:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test additions/changes
- `chore`: Maintenance tasks

### Architecture Guidelines

- Follow Clean Architecture principles
- Keep layers separated
- Use dependency injection
- Write testable code

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint
```

## Documentation

- Update README.md for user-facing changes
- Update technical docs for architecture changes
- Add KDoc comments for public APIs
- Include examples where helpful

## Release Process

1. Update version in `build.gradle.kts`
2. Update CHANGELOG.md
3. Create pull request to `main`
4. After merge, tag release
5. Create GitHub release

## Questions?

Feel free to open an issue with the `question` label or reach out to the maintainers.

Thank you for contributing! 🚀