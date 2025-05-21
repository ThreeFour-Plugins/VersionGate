# Contributing to VersionGate

Thank you for considering contributing to VersionGate! This document outlines how to contribute to the project.

## Code of Conduct

Please be respectful and considerate of others when contributing to this project. We aim to foster an inclusive and welcoming community.

## How to Contribute

There are many ways to contribute to VersionGate:

1. **Reporting Bugs**: Use the bug report template to submit detailed bug reports.
2. **Suggesting Features**: Use the feature request template to suggest new features.
3. **Submitting Pull Requests**: Code contributions are welcome!
4. **Improving Documentation**: Help improve the documentation to make it more clear and useful.

## Development Setup

1. **Fork the repository**

2. **Clone your fork**
   ```
   git clone https://github.com/YOUR_USERNAME/VersionGate.git
   cd VersionGate
   ```

3. **Set up the development environment**
   ```
   ./gradlew build
   ```

4. **For documentation development**
   ```
   npm install
   npm run docs:dev
   ```

## Pull Request Process

1. Create a new branch for your feature or bugfix:
   ```
   git checkout -b feature/your-feature-name
   ```

2. Make your changes, ensuring they follow the project's coding style.

3. Add tests for your changes if applicable.

4. Run the tests to ensure they pass:
   ```
   ./gradlew test
   ```

5. Commit your changes with a clear and descriptive commit message:
   ```
   git commit -m "Add feature: your feature description"
   ```

6. Push to your branch:
   ```
   git push origin feature/your-feature-name
   ```

7. Create a pull request on GitHub.

## Coding Standards

- Follow Java coding conventions
- Include comments where necessary
- Write clear commit messages
- Update documentation as needed

## Documentation Development

The documentation is built using VitePress. When making changes to the documentation:

1. Make your changes to the files in the `docs/` directory
2. Preview your changes locally with `npm run docs:dev`
3. Build the documentation with `npm run docs:build` to make sure it builds correctly

## License

By contributing to VersionGate, you agree that your contributions will be licensed under the project's MIT License. 