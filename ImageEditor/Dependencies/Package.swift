// swift-tools-version: 5.10

import PackageDescription

let package = Package(
    name: "Dependencies",
    platforms: [
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "_Alloy",
            targets: ["_Alloy"]
        ),
        .library(
            name: "_Lottie",
            targets: ["_Lottie"]
        )
    ],
    dependencies: [
        .package(
            url: "https://github.com/airbnb/lottie-ios",
            .upToNextMajor(from: "4.0.0")
        ),
        .package(
            url: "https://github.com/s1ddok/Alloy.git",
            .upToNextMajor(from: "0.18.0")
        )
    ],
    targets: [
        .target(
            name: "_Alloy",
            dependencies: [
                .product(
                    name: "Alloy",
                    package: "Alloy"
                ),
            ]
        ),
        .target(
            name: "_Lottie",
            dependencies: [
                .product(
                    name: "Lottie",
                    package: "lottie-ios"
                )
            ]
        )
    ]
)
