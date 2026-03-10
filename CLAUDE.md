# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Affine is an iOS app for visualizing and interactively editing Jackson Structured Programming (JSP) tree diagrams. It is a direct Swift/SwiftUI port of a Java Jackson tree visualization tool.

## Build & Test Commands

```bash
# Build
xcodebuild -project Affine.xcodeproj -scheme Affine build

# No test targets currently exist
```

## Architecture

- **SwiftUI** with Canvas API for high-performance 2D rendering
- **MVVM**: `TreeViewModel` (@Observable) manages state; `StructuredNode` is the data model
- **Layout**: Recursive Jackson tree layout algorithm in `JacksonTreeLayout.swift`

### Key Files

| File | Role |
|------|------|
| `Models/StructuredNode.swift` | Core tree node data structure with parent-child management |
| `Models/NodeTypes.swift` | 4 JSP node types (Sequence, Selection, Iteration, Fundamental) |
| `Models/TreeViewModel.swift` | Observable state, tap/gesture handling, add/delete operations |
| `Layout/JacksonTreeLayout.swift` | Recursive layout engine, hit-testing, node positioning |
| `Views/TreeCanvasView.swift` | Canvas rendering with connector lines and type symbols |
| `Views/ContentView.swift` | Main UI with toolbar, context menu, zoom controls |

## Score Package — Shared Base Classes

The [Score](../score) package (`import Score` / `import ScoreUI`) is a shared local SPM library providing financial and utility base types used across sibling projects. While this project does not currently depend on Score, the following types are available if financial functionality is needed:

| Type | Module | Description |
|------|--------|-------------|
| `Money` | Score | Currency-safe monetary amounts with `Decimal` precision. Arithmetic enforces matching currencies. |
| `Currency` | Score | ISO 4217 enum with 180+ currencies, decimal places, and localized names. |
| `Percent` | Score | Percentage as factor (e.g. `0.10` = 10%). |
| `FXRate` | Score | Bid/ask exchange rates with conversion methods. |
| `VATCalculation` | Score | VAT split (net/gross) with inclusive/exclusive handling. |
| `YearMonth` | Score | Year-month value type for monthly periods. |
| `DayCountRule` | Score | Financial day count conventions (ACT/360, ACT/365, 30/360). |
| `ServicePipeline` | Score | Async middleware chain for service operations. |
| `ServiceError` | Score | Typed errors (notFound, validation, businessRule, etc.). |
| `CSVExportable` | Score | Protocol for CSV row export. |
| `IBANValidator` | Score | ISO 13616 IBAN validation. |
| `SCORReferenceGenerator` | Score | ISO 11649 creditor reference with Mod 97. |
| `ErrorHandler` | ScoreUI | Observable error state management for SwiftUI. |
| `PDFRenderer` | ScoreUI | UIKit-based PDF generation. |
| `.errorAlert()` | ScoreUI | SwiftUI modifier for error alert presentation. |

To add Score as a dependency, add a local package reference to `../score` in Xcode.
