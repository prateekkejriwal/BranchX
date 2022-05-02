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

## How it works?
![Flow Diagram](https://lh3.googleusercontent.com/fife/AAWUweXRPxDIGLqeurWxqXh4i1h89Pmc4CN6d1aZ0cr66a70s-8CEjhmgzYhVEGsaBmOLX0oYHW7aSOJslbWuVoyayJD9AkxunGX3yCMQ8Zhcz08JgszAiB06TGmhO0DevQejcFZNdFlBpwgEkWiNCkD1FlRRDZGD3YYP2Z-UkblWHXdFQrvRLOI8VTTVNLn3Ch_P9h7UQohxn4-cv9rHe0Y9jmbulSuUL6obLAVN9u4LNoeBM-kn6cozlbvqDGZZWBYDLCfs00hmMNWR_KScOnnaup5ytGQRq0f33zEu5Y1ygywfsRnim9lPWQp9A87xu2G6_iB3AJFGgzamTK2AXUo9SE43KtobYNudRuoN9QrLK7Cw8GDc3spfMQRmnPowXYr7pqZik_zhVtC48cD69IJQVV8o5Z_Wey6nRs04guMqQiBFz16OhPDAaBEu6KzN1atUyALj495zyOGDQsinzKJr90pCfPJpYbqw6lz4blTBtpgzNyxtHrEYXhYKN0f1zi_hyKymfOZL_uKZXKw0_HAl9uIBt1MOIWBrsMiH7g1MqjxLMnLls0Uvj7NA-1LaeydSrj6EulSBweP1GlaHq5RMX9hXzt_Bc8vmA6AJDW4ZvVETmjgVQ-w7jr3xkY5Zb_BvQdG8gAp-B9VJSgT0x5Ylv3x2ACOLgApxphsV6ZUhlqofq0FfvDVLaEc6LzLdr-caqfaAiSscbSnkDQ91IRL0H8oh5tCpK9OhvMjUYS6SIHg7SNjrk30y3JegOuPX9TZwdcWqMwJY9SDYF0_HSk=w640-h400-k)

## Settings Screenshot

![Sample UI ](https://lh3.googleusercontent.com/fife/AAWUweVlnhj7tkMXRdiJxhcxa55KAiHCkf80X8Xvku44Rm-EnPw6WyvRrX_NL9d7VC9Xn7ZMnvQ23eFH30PgMzwymmOn4yG8nrMQpCDo4bGXMI4WXbsr4MlDJcIkBoCcYpc1Hc7g0GM77rkQ79Y9wuIt_l-rOlvrPvuDoqCAJdht5uA90udCsaJKPduA20_eoto_25a4TrAG7BTHZvPKBMA8p0A_JsfoaKDpuqPmoWXoVdzATb7JhLUo8feMCuyVyvMcp3LYZgx_ok_JeJLCGLIsC8_y19ioVKDFYtwtLazsVJ8qVhMmPZTYiD3mmA5GoGXs7KdDM8QM837WkRKHoMW5LY62zYrHcqNK-XqKSkCOUie1j0aa1apcWOHonIJ92y9NmmvyMa915Qkj-5C13GgcR-jC1mQTwGYLrv0vNq6kTTFfGvVJcbv8pAPEmQL7-xhcpRV5gznkENgtMAtwLdl7CMYFNhsiF06z3SufUT4s_eJErrHig5APMCabUjk3hOdNm5d0jbWVxUwkJgVSHc9tHocGBueTi70JRLrJ55-Zp0zTxZR_ez_Nv31qUvEhy_FNaB77sQbR_OH0SQ7HygFFgJtfsuOPG7xepZMr4Hr4pLsNfAQ6E7UboUZIK-Tw4pwTehY9ZO5kqZLc_EqsEd4QIpU0MOZh1gJSXa57Mu_JoUa7nbQWbvSTwe2WD7cAlNT1cdWCWRS4Fx8PFLgZQfzbNrNIHu2bTWiPdqjHFXP6XMu0Y5JJFXUNMvMgs6KXsP82CD-hEBJ_vDxZJ7IUrO4=w640-h400-k)


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