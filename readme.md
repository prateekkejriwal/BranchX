# BranchX

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
## What is it?
BranchX is a wrapper over core Branch SDK that allows the client to have more control over Branch implementation and push any updates over the air using AppID as an identifier.

## Some use cases

* The SDK can be disabled OTA.
* It makes debugging a lot easier for SIEs / SEs.
* Events can be capped to 1 instance per X seconds reducing redundancy.
* Fix any typos in event names without a fresh deployment.
* Near real-time configuration updates.
* Enable / Disable features on the SDK.

## Reference Document and Deck

* [Reference Document](https://docs.google.com/document/d/18Ot7dFN8kZShU0_vxFMoISkaKVXgiYktaxb6SnZoRp8/edit)
* [Reference Deck](https://docs.google.com/presentation/d/1nwjqZ6KP7VXa3ULiMWiRDxTda7NWwdV6OGJFN4Qzmw4/edit#slide=id.g11864839c55_0_450)

## How to run this on your set up?

1. Clone this repository.
2. Import the project into Android Studio.
3. Open Gradle.properties and update the following values -
  1. branchX.appId=YOUR_APP_ID_HERE
  2. branchX.server=BRANCH_CONFIGURATION_HOST_HERE
4. Run the configuration middleware.
5. Build and install the application on your device.

## License

The project is under GNU GPL License V3.0. A copy of the license can be found at the root level of this repo.