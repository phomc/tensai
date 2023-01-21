# Contributing Guide

Thank you for your decision to contribute to Tensai!

## Workflow
To start making changes, let's have an overview about the workflow:
1. Fork a Tensai repo
2. Make changes to your fork
3. TEST IT CAREFULLY
4. Submit a PR
5. Resolve requested changes to your PR
6. Wait for acceptance from a team member
7. Your PR is now merged

You contribution may involve in three different repos:
- Tensai API
- Tensai client
- Docs

It is important to satisfy the contribution requirement by supplying adequate changes to one or more repos aforementioned.<br>
E.g: If you are going to add a feature. Please ensure you will develop its implementation in client-side and server-side, its API and the relevant documentation.

## Requirements
- Follow the Google Style for Java: https://google.github.io/styleguide/javaguide.html
- In addition, make sure that:
  + Line ending is Unix-like `\n`
  + DON'T make unnecessary changes: refactoring, renaming, add new dependencies, etc. If needed, it is better to merge these changes in a single commit.
  + DON'T hardcode values. Use environment variables or configuration.
  + DON'T comment too much if it is unneeded.
  + DON'T write utility methods if they are already provided by external dependencies.
  + DON'T mix different changes in a single PR. One PR must have only one feature, or a group of relevant bug fixes.

*For project maintainers*
- DON'T merge any PR straight away. Give the community some time for discussion first.
- Make sure the PR owner has tested carefully and resolved requested changes.
- Please **squash merge the PR**
