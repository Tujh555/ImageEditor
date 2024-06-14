// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Modules",
    platforms: [
        .iOS(.v17)
    ],
    products: [
        .library(
            name: "App",
            targets: ["App"]
        ),
        .library(
          name: "WelcomeFeature",
          targets: ["WelcomeFeature"]
        ),
    ],
    dependencies: [
        .package(path: "../Core"),
        .package(path: "../Dependencies")
    ],
    targets: [
        .target(
            name: "App",
            dependencies: [
                "Core",
                "WelcomeFeature",
                .product(
                    name: "_Lottie",
                    package: "Dependencies"
                )
            ],
            resources: [
              .process("Resources")
            ]
        ),
        .target(
            name: "WelcomeFeature",
            dependencies: [
                "Core",
                .product(
                    name: "_Lottie",
                    package: "Dependencies"
                )
            ],
            resources: [
              .process("Resources")
            ]
        )
    ]
)
