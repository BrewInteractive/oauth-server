# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [2.0.3](https://github.com/BrewInteractive/oauth-server/compare/v2.0.2...v2.0.3) (2024-04-03)

### [2.0.2](https://github.com/BrewInteractive/oauth-server/compare/v2.0.1...v2.0.2) (2024-04-03)


### Bug Fixes

* fixed get custom claims endpoint. ([4b7deb3](https://github.com/BrewInteractive/oauth-server/commit/4b7deb3aef24de09e0cf459952046bfde4a49657))

### [2.0.1](https://github.com/BrewInteractive/oauth-server/compare/v2.0.0...v2.0.1) (2024-04-03)

## [2.0.0](https://github.com/BrewInteractive/oauth-server/compare/v1.11.2...v2.0.0) (2024-04-03)


### ⚠ BREAKING CHANGES

* clients won't send additional_claims parameter to /oauth/token endpoint.

### Features

* error page redirect ([c56f974](https://github.com/BrewInteractive/oauth-server/commit/c56f974c7839c256360ac91da972ea70e2a74f4e))
* implemented CustomClaimService. ([6aed317](https://github.com/BrewInteractive/oauth-server/commit/6aed317583bde54b3079e781cb00ed21c89dd96e))
* remove additional_claims parameter from TokenRequestModel and add custom claim hook service. ([02f714f](https://github.com/BrewInteractive/oauth-server/commit/02f714f1142429794147608f190665f9cd20aeb2))
* removed invalid parameters while redirecting to redirect uri. ([2c13476](https://github.com/BrewInteractive/oauth-server/commit/2c13476cec602a0cf4553ac96de2196f8d4a9a5e))


### Bug Fixes

* authorization header must be start with "Basic". ([b47ee92](https://github.com/BrewInteractive/oauth-server/commit/b47ee927bde45e4705bbd8b0e280f1638004fddd))
* fixed /authorize tests. ([ba43ffb](https://github.com/BrewInteractive/oauth-server/commit/ba43ffb43cae292f1175cf1200a137c827741061))
* fixed entity. ([a595659](https://github.com/BrewInteractive/oauth-server/commit/a5956591e04cb89cba1b99eed22c6f51e1e6157f))
* fixed error returns. ([4676c4a](https://github.com/BrewInteractive/oauth-server/commit/4676c4a3780ac0fc82e2a34d0882b2abd22b4c9c))
* fixed TokenGrantProviderAuthorizationCode tests. ([92cdadf](https://github.com/BrewInteractive/oauth-server/commit/92cdadfb86f5f62965b69a52f448399c60ead2b8))
* fixed TokenGrantProviderClientCredentials tests. ([db40475](https://github.com/BrewInteractive/oauth-server/commit/db40475b3b511f7867ea4164d6adb1bd99482a32))
* fixed TokenGrantProviderRefreshToken tests. ([a3954bf](https://github.com/BrewInteractive/oauth-server/commit/a3954bfa7a3ed5a29779c8c2f9a3791195b0d4f7))
* fixing error messages. ([49e8336](https://github.com/BrewInteractive/oauth-server/commit/49e8336caf0db9fbb10d754e869821499ec2ab63))
* make scope nullable. ([ab7b75a](https://github.com/BrewInteractive/oauth-server/commit/ab7b75a1d3ff1778f1191b0570acae38ca951f61))
* return as HashMap. ([80d1a40](https://github.com/BrewInteractive/oauth-server/commit/80d1a40b2193effc9c314ebef41eb75ec5b0247c))

### [1.11.2](https://github.com/BrewInteractive/oauth-server/compare/v1.11.1...v1.11.2) (2024-03-11)

### [1.11.1](https://github.com/BrewInteractive/oauth-server/compare/v1.11.0...v1.11.1) (2024-03-11)

## [1.11.0](https://github.com/BrewInteractive/oauth-server/compare/v1.10.2...v1.11.0) (2024-02-28)


### Features

* make audience and issuer_uri nullable. ([a14cb82](https://github.com/BrewInteractive/oauth-server/commit/a14cb827dd82174542df456e301c5f5b4a3173d2))

### [1.10.2](https://github.com/BrewInteractive/oauth-server/compare/v1.10.1...v1.10.2) (2024-02-14)


### Bug Fixes

* fix url patterns for corsfilter ([4b84d75](https://github.com/BrewInteractive/oauth-server/commit/4b84d759b7bbab03f937fb612127728e41b34b45))

### [1.10.1](https://github.com/BrewInteractive/oauth-server/compare/v1.10.0...v1.10.1) (2024-02-13)

## [1.10.0](https://github.com/BrewInteractive/oauth-server/compare/v1.9.0...v1.10.0) (2024-02-13)


### Features

* **TMID-873:** added body to error response. ([a21b5cd](https://github.com/BrewInteractive/oauth-server/commit/a21b5cd2496bfabde7ec5a73f99ec96924104077))
* **TMID-873:** added check for consent endpoint. ([c0b824f](https://github.com/BrewInteractive/oauth-server/commit/c0b824ffe68b6fe84a2309c413410ddb40277c66))
* **TMID-873:** added check for consent endpoint. ([9ebf29a](https://github.com/BrewInteractive/oauth-server/commit/9ebf29a190cef9a9de52810e232c6ae46957ea2a))
* **TMID-873:** added check for request type. ([b1c38e4](https://github.com/BrewInteractive/oauth-server/commit/b1c38e48f3c7dd6653b4ac7fbea0dfd5549c0611))
* **TMID-873:** added implicit grant for test cases. ([7fd26ee](https://github.com/BrewInteractive/oauth-server/commit/7fd26ee3a2a4ce92b2643e50af810fd88a6c3b0b))
* **TMID-873:** added scope parameter to /authorize endpoint. ([d17ab71](https://github.com/BrewInteractive/oauth-server/commit/d17ab714b8b3d9aacef688357f9d4e64fdc8646a))
* **TMID-873:** added scope to authorization codes. ([2867879](https://github.com/BrewInteractive/oauth-server/commit/28678796cf9dcfad823d8a3e427291e5b7c173de))
* **TMID-874:** added scope parameter to validate method. ([810cc04](https://github.com/BrewInteractive/oauth-server/commit/810cc04eb743667bbd4c089d65874a8cefae6564))
* **TMID-875:** added clients_scopes relation. ([8f1cebf](https://github.com/BrewInteractive/oauth-server/commit/8f1cebfbea89cd7e5560018bf7d6a3f35f5674eb))
* **TMID-875:** added scope validation to ClientValidator. ([da6d00d](https://github.com/BrewInteractive/oauth-server/commit/da6d00dac9e05624a7f35574a32bac41537ec03a))
* **TMID-875:** added scopeList. ([30a5e2b](https://github.com/BrewInteractive/oauth-server/commit/30a5e2b8b6864b7b76e84c099cc8e8b2fb8ea2a7))
* **TMID-875:** added ScopeModel class. ([5e5f308](https://github.com/BrewInteractive/oauth-server/commit/5e5f308fa38c6aa5930011ed03fa2c09b70ab193))
* **TMID-881:** add ScopeValidator ([3bcc33c](https://github.com/BrewInteractive/oauth-server/commit/3bcc33c5441e478d1c5b0a30420df9e61ed770a8))
* **TMID-883:** refactor ClientUserService.getOrCreate ([cb852c9](https://github.com/BrewInteractive/oauth-server/commit/cb852c9ba9cffda5166a706cdd5ebc18dbe6e0ec))
* **TMID-933:** added azp and scope claims. ([22839fd](https://github.com/BrewInteractive/oauth-server/commit/22839fdd2d63fecdcaed8fa1da6d332f8d3429c5))
* **TMID-934, TMID-931:** added generating id token. ([c14db7b](https://github.com/BrewInteractive/oauth-server/commit/c14db7b84cf587d2dd649bd867e7b4bd1e467f63))
* **TMID-934:** add ENABLE_ID_TOKEN env. ([4de3620](https://github.com/BrewInteractive/oauth-server/commit/4de36205f6bf347d80fae28d13f36e7fa1af7f14))
* **TMID-935:** add UserIdentityService ([fbbb270](https://github.com/BrewInteractive/oauth-server/commit/fbbb270beaadd30c8058224bd355e480f75e9661))


### Bug Fixes

* **TMID-873:** typo on oauth.consent_endpoint config ([baae558](https://github.com/BrewInteractive/oauth-server/commit/baae5580c714e419e4d1ef6ef5d64a704ca37d3a))
* **TMID-934:** fix userIdentityInfo fixture random map count to 2 ([7e1db3f](https://github.com/BrewInteractive/oauth-server/commit/7e1db3f143cd6ccee9c7f8ce5547085b22d445a8))
* **TMID-935:** generify user info model response ([28081e1](https://github.com/BrewInteractive/oauth-server/commit/28081e16ff1d1b00e04c3a034f053467038c3fc5))

## [1.9.0](https://github.com/BrewInteractive/oauth-server/compare/v1.8.0...v1.9.0) (2024-01-19)


### Features

* **TMID-930:** removed jwt.secret.key env. ([77bb887](https://github.com/BrewInteractive/oauth-server/commit/77bb887475bc7bd4287cde3814102a5e5eff235b))
* **TMID-930:** sign tokens with client secret instead of jwt.secret.key env. ([9e254fe](https://github.com/BrewInteractive/oauth-server/commit/9e254fee0d428691f859d18757e1748ba363490a))


### Bug Fixes

* **TMID-853:** remove phone related properties from UserCookieModel,UserCookieModelTest,UserCookieManagerTest ([d3e7a18](https://github.com/BrewInteractive/oauth-server/commit/d3e7a1876a470f7a2baf091b6b252d736fa110f8))

## [1.8.0](https://github.com/BrewInteractive/oauth-server/compare/v1.7.8...v1.8.0) (2024-01-15)


### Features

* **TMID-555:** swagger endpoint created ([1b9ace0](https://github.com/BrewInteractive/oauth-server/commit/1b9ace0da7c92d54ac972701acfce68fdf760b27))


### Bug Fixes

* **TMID-537:** remove unnecessary client upload endpoint, aws, s3 dependency, file upload providers ([a0a6ef2](https://github.com/BrewInteractive/oauth-server/commit/a0a6ef2aa4c971a51d116391f1823adc0cf2b20e))
* **TMID-537:** remove unnecessary client upload endpoint, aws, s3 dependency, file upload providers ([976cc0b](https://github.com/BrewInteractive/oauth-server/commit/976cc0b46897f882e68fbc67a4c44cc0fb1ee552))
* **TMID-555:** removed isSwaggerUIRequest method in CORSFilter.java and add urlPattern in FilterConfig.java ([a7d2daa](https://github.com/BrewInteractive/oauth-server/commit/a7d2daa5ebf7f5d695bf308f554b87826dcacb8c))
* **TMID-555:** Updated isSwaggerUIRequest method in CORSFilter.java ([0787166](https://github.com/BrewInteractive/oauth-server/commit/0787166a1791528b99b9d63f45c8db5476fe0e71))

### [1.7.8](https://github.com/BrewInteractive/oauth-server/compare/v1.7.7...v1.7.8) (2023-08-17)


### Bug Fixes

* add override for request.getInputStream in CORSFilter ([515fc92](https://github.com/BrewInteractive/oauth-server/commit/515fc924080755f2506579a33a30422c54f19f52))

### [1.7.7](https://github.com/BrewInteractive/oauth-server/compare/v1.7.6...v1.7.7) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Handle preflight-requests. ([81290a9](https://github.com/BrewInteractive/oauth-server/commit/81290a9bfca0a1446ecdc603cce7d9bf3af442e8))

### [1.7.6](https://github.com/BrewInteractive/oauth-server/compare/v1.7.5...v1.7.6) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Handle preflight-requests. ([8bd9d79](https://github.com/BrewInteractive/oauth-server/commit/8bd9d794422d901b48b49e230aa5ab6f8cef07b6))

### [1.7.5](https://github.com/BrewInteractive/oauth-server/compare/v1.7.4...v1.7.5) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Handle preflight-requests. ([351b4d1](https://github.com/BrewInteractive/oauth-server/commit/351b4d1300a012b1547ef98397bf3715a612db16))

### [1.7.4](https://github.com/BrewInteractive/oauth-server/compare/v1.7.3...v1.7.4) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Add cors configuration in OPTIONS request also. ([da500a0](https://github.com/BrewInteractive/oauth-server/commit/da500a090a0e20b8386e92d3836f88c42d395588))

### [1.7.3](https://github.com/BrewInteractive/oauth-server/compare/v1.7.2...v1.7.3) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Fixed adding cors configuration. ([5760de1](https://github.com/BrewInteractive/oauth-server/commit/5760de16fc908012558b49b2105c7357bb0583c4))

### [1.7.2](https://github.com/BrewInteractive/oauth-server/compare/v1.7.1...v1.7.2) (2023-08-17)


### Bug Fixes

* **TMID-416, TMID-418:** Modified adding cors configuration. ([e9b13f1](https://github.com/BrewInteractive/oauth-server/commit/e9b13f155da2917159029890be4bbd7b8b1e9cdb))

### [1.7.1](https://github.com/BrewInteractive/oauth-server/compare/v1.7.0...v1.7.1) (2023-08-16)


### Bug Fixes

* **TMID-416, TMID-418:** Replace "Origin" header check with "Referrer" check since "Origin" may be null. ([f3b9a55](https://github.com/BrewInteractive/oauth-server/commit/f3b9a55caeedf8e936bb84a8ba4be5571d59c095))

## [1.7.0](https://github.com/BrewInteractive/oauth-server/compare/v1.6.0...v1.7.0) (2023-08-04)


### Features

* **TMID-369:** add additional_claims to token endpoint requests model ([1256372](https://github.com/BrewInteractive/oauth-server/commit/1256372b48e93efc0d113ab8859281672622dee3))
* **TMID-387:** Added CORSMiddleware. ([25d026b](https://github.com/BrewInteractive/oauth-server/commit/25d026b7a00deeecbef5e30fdd8adad46ab88b0f))
* **TMID-387:** added unit test for clientService. ([5b7e675](https://github.com/BrewInteractive/oauth-server/commit/5b7e67530942f54fa54ccfe4c4a12501c87246d5))
* **TMID-387:** added unit test for CORSMiddleware. ([265fc30](https://github.com/BrewInteractive/oauth-server/commit/265fc30cd5ada5bdc435c0225e518cea806e4101))
* **TMID-387:** added unit tests.. ([b56a6c7](https://github.com/BrewInteractive/oauth-server/commit/b56a6c7c6ceb347970a2acce727b4834730a07f2))
* **TMID-387:** Added WebOrigin entity. ([0625343](https://github.com/BrewInteractive/oauth-server/commit/06253436f689bbceb88051a297c4f11ecf99b777))
* **TMID-387:** Added WebOriginModel. ([67cffb8](https://github.com/BrewInteractive/oauth-server/commit/67cffb888a7dcd1a26d9095085bc62d94f8880bd))
* **TMID-387:** fixed integration test. ([635759b](https://github.com/BrewInteractive/oauth-server/commit/635759b2f8b587be5aca312a469367b9421f77ba))
* **TMID-387:** Implemented CORSMiddleware. ([a1f7358](https://github.com/BrewInteractive/oauth-server/commit/a1f73585a2cf9dcd2bdbe3145060c85351bfdc2b))


### Bug Fixes

* fix UserCookieModel parser ([70cad6a](https://github.com/BrewInteractive/oauth-server/commit/70cad6a27a56ee0605b22edb889db0a03ad8ef7d))
* fix UserCookieModel parser as safe ([24e3951](https://github.com/BrewInteractive/oauth-server/commit/24e395108249ef4e3c9a4e0ea998b72f6f4740ca))
* **TMID-387:** remove unnecessary dependencies. ([9ec0f06](https://github.com/BrewInteractive/oauth-server/commit/9ec0f06881e86d615bde1f86229eddee908d9e24))

## [1.6.0](https://github.com/BrewInteractive/oauth-server/compare/v1.5.0...v1.6.0) (2023-07-25)


### Features

* **TMID-296:** add set client logo post endpoint ([b0bc876](https://github.com/BrewInteractive/oauth-server/commit/b0bc876112ac276bb7ab6b0f6024946b69dfd0c5))
* **TMID-296:** add set client logo post endpoint ([a3bde7d](https://github.com/BrewInteractive/oauth-server/commit/a3bde7dc3ff3ee56ec921edd0ff77e50fa620b60))
* **TMID-351:** add S3StorageProvider, move factories to service/factory ([5f9c2b7](https://github.com/BrewInteractive/oauth-server/commit/5f9c2b791568e146b15b074404c8990acfbefef4))
* **TMID-352:** add BaseFileStorageProvider, FileStorageProvider, FileStorageProviderFactory ([4c7c4ec](https://github.com/BrewInteractive/oauth-server/commit/4c7c4ec3bf83b674f0f1413217ed1530af3d8918))
* **TMID-357:** changed user id related fields from long to string. ([7188584](https://github.com/BrewInteractive/oauth-server/commit/71885840f98708527cfc3b4e6b154955c0fec1c0))
* **TMID-360:** add user_id to redirected query params in authorize ([406efa8](https://github.com/BrewInteractive/oauth-server/commit/406efa893fcb3dc00594631ddb4f849a9d23cb46))
* **TMID-370:** add TokenCookieManager.setTokens ([576572f](https://github.com/BrewInteractive/oauth-server/commit/576572f42941bf97fbd4e65aad0b028c2ff8a23c))


### Bug Fixes

* **TMID-350:** create setClientLogo service method on ClientService ([1d3d700](https://github.com/BrewInteractive/oauth-server/commit/1d3d70035be87b0d1566fc77649df7b0a14da90f))
* **TMID-351:** add new illegal argument cases ([5dd1690](https://github.com/BrewInteractive/oauth-server/commit/5dd16902346322c1a9afe631907d8e2f46722317))
* **TMID-351:** cleanup ([02ccc6c](https://github.com/BrewInteractive/oauth-server/commit/02ccc6ce34beb0edee50da81fa04e33996c532ba))
* **TMID-357:** fixed conflicted. ([47ef15c](https://github.com/BrewInteractive/oauth-server/commit/47ef15c6f35438fdb96ccb50c955d778614a5bfa))
* **TMID-357:** fixed user id format in test cases. ([9db60ab](https://github.com/BrewInteractive/oauth-server/commit/9db60ab76e21bb65658ea418a0109d3146765dcb))
* **TMID-357:** removed typo. ([63348c2](https://github.com/BrewInteractive/oauth-server/commit/63348c2a9a67b192aef3f12c2e33bc161496a251))
* **TMID-357:** resolved minor conflict. ([1639386](https://github.com/BrewInteractive/oauth-server/commit/1639386bb4b99e4155a5f4b753a80c64bba9fd14))

## [1.5.0](https://github.com/BrewInteractive/oauth-server/compare/v1.4.0...v1.5.0) (2023-07-10)


### Features

* **TMID-297:** refactor user cookie format ([ea8c4d3](https://github.com/BrewInteractive/oauth-server/commit/ea8c4d33c78d7a869c22942aaf1e04b55e12020d))

## [1.4.0](https://github.com/BrewInteractive/oauth-server/compare/v1.3.9...v1.4.0) (2023-07-04)


### Features

* **TMID-282:** add validator for conditional redirect uri requirement ([9874e70](https://github.com/BrewInteractive/oauth-server/commit/9874e704067105a0308f581d4533706b2a2454a1))


### Bug Fixes

* **TMID-282:** add coverage for token request model validator ([7b113e0](https://github.com/BrewInteractive/oauth-server/commit/7b113e07d1680943c4f10d0b01a72e8c10d65a43))

### [1.3.9](https://github.com/BrewInteractive/oauth-server/compare/v1.3.8...v1.3.9) (2023-06-14)

### [1.3.8](https://github.com/BrewInteractive/oauth-server/compare/v1.3.7...v1.3.8) (2023-06-14)

### [1.3.7](https://github.com/BrewInteractive/oauth-server/compare/v1.3.6...v1.3.7) (2023-06-13)

### [1.3.6](https://github.com/BrewInteractive/oauth-server/compare/v1.3.3...v1.3.6) (2023-06-13)

### [1.3.5](https://github.com/BrewInteractive/oauth-server/compare/v1.3.3...v1.3.5) (2023-06-13)

### [1.3.4](https://github.com/BrewInteractive/oauth-server/compare/v1.3.3...v1.3.4) (2023-06-13)

### [1.3.3](https://github.com/BrewInteractive/oauth-server/compare/v1.3.2...v1.3.3) (2023-06-13)

### [1.3.2](https://github.com/BrewInteractive/oauth-server/compare/v1.3.1...v1.3.2) (2023-06-09)

### [1.3.1](https://github.com/BrewInteractive/oauth-server/compare/v1.3.0...v1.3.1) (2023-05-04)


### Bug Fixes

* fix parameter conversion. ([7c5c967](https://github.com/BrewInteractive/oauth-server/commit/7c5c9676def2552c2e6e35edd31300ce84b6e6a8))
* fix parameter conversion. ([9d5b5b8](https://github.com/BrewInteractive/oauth-server/commit/9d5b5b803713a64c1ce53df6c5d3a9cd8b769e0c))

## [1.3.0](https://github.com/BrewInteractive/oauth-server/compare/v1.2.2...v1.3.0) (2023-05-04)


### Features

* **TMID-162:** add EncryptionUtils class. ([740279b](https://github.com/BrewInteractive/oauth-server/commit/740279b5aa4d670b6a0b937f4cc70f2ca978dd36))
* **TMID-162:** convert cookie key to "user". ([9ad17f7](https://github.com/BrewInteractive/oauth-server/commit/9ad17f79e76d34f6b59b2f359b5946a89315c5d3))
* **TMID-162:** implement user cookie manager. ([2feae75](https://github.com/BrewInteractive/oauth-server/commit/2feae75aa9433be313a7069f9001066f1db0637e))
* **TMID-162:** refactored redirecting to login. ([f7f131c](https://github.com/BrewInteractive/oauth-server/commit/f7f131cdeb754a5e27906ece78ae50eeea3aa9f7))
* **TMID-162:** refactored redirecting to login. ([1745fb6](https://github.com/BrewInteractive/oauth-server/commit/1745fb64de0619462840e8f54ffc66909db56ad9))


### Bug Fixes

* fixed invalid environment variable. ([e73ddf8](https://github.com/BrewInteractive/oauth-server/commit/e73ddf8b8c16736ba44b7517320a1de17ce2fa44))

### [1.2.2](https://github.com/BrewInteractive/oauth-server/compare/v1.2.1...v1.2.2) (2023-04-18)


### Bug Fixes

* authorization code expires ms value from environment value ([78da1c9](https://github.com/BrewInteractive/oauth-server/commit/78da1c9008e0cfebe0118e8f699ed0a3d466bc84))

### [1.2.1](https://github.com/BrewInteractive/oauth-server/compare/v1.2.0...v1.2.1) (2023-04-13)


### Bug Fixes

* fix expiresAt value, ([c82ae3a](https://github.com/BrewInteractive/oauth-server/commit/c82ae3a243a9143ae3a42c513c50611f837b0115))

## [1.2.0](https://github.com/BrewInteractive/oauth-server/compare/v1.1.0...v1.2.0) (2023-04-11)


### Features

* **MOB-100:** add TokenGrantProviderFactory new service ([f17375e](https://github.com/BrewInteractive/oauth-server/commit/f17375e3c3f24fefa4f4a33050498d018d870a5f))
* **MOB-100:** add TokenGrantProviderRefreshToken.generateToken and unit tests ([b23b03a](https://github.com/BrewInteractive/oauth-server/commit/b23b03a378356ccbddfdd5f5f406ab41255b79a9))
* **MOB-100:** add TokenGrantProviderRefreshToken.validate and tests ([1dcdaac](https://github.com/BrewInteractive/oauth-server/commit/1dcdaac245a12a53b466d33756d60f1279b5724b))
* **MOB-100:** fix RefreshTokenService.revokeRefreshToken ([79d2418](https://github.com/BrewInteractive/oauth-server/commit/79d24183e4d09e4cfd56e4143fb8223482863be2))
* **MOB-100:** fix RefreshTokenService.revokeRefreshToken ([86ac648](https://github.com/BrewInteractive/oauth-server/commit/86ac648476247a616374fedc75db5ab0e848280b))
* **MOB-110:** add AuthorizeController.tokenPost ([561102d](https://github.com/BrewInteractive/oauth-server/commit/561102dfa2b8da9a7b3aee772490de4af40e9df1))
* **MOB-99:** add TokenGrantProviderClientCredentials and unit tests ([b990fd6](https://github.com/BrewInteractive/oauth-server/commit/b990fd67b00f6d19098f12c81b2e0613f6e03a7b))
* **MOB-99:** add unit test case to prevent partial coverage ([3b9a7a4](https://github.com/BrewInteractive/oauth-server/commit/3b9a7a4660d98d99b33fce3135c8379fdd8a9621))
* **MOB-99:** alter jwt and token services to generate token without subject ([027af8f](https://github.com/BrewInteractive/oauth-server/commit/027af8fd600c541633010b8a2871c02ffc560d13))
* **TMID-100:** remove constructor, add autowired ([d26e2af](https://github.com/BrewInteractive/oauth-server/commit/d26e2afa60634d7da30dd9029b4ffbd7d76fdb87))
* **TMID-103:** added issue_refresh_tokens column. ([eb1fa8b](https://github.com/BrewInteractive/oauth-server/commit/eb1fa8bdba1e6fe1833531608732f72c6010235f))
* **TMID-104:** added JwtService. ([9e517f0](https://github.com/BrewInteractive/oauth-server/commit/9e517f07070559d88979e75657e70d4bf34a33ab))
* **TMID-104:** added JwtServiceImpl. ([741de56](https://github.com/BrewInteractive/oauth-server/commit/741de5658c46e1b281198c9ac7aa72dc916b927e))
* **TMID-105:** implemented signToken(subject, audience, issuerUri, state, tokenExpiresInMinutes, signingKey, additionalClaims, refreshToken). ([50510cd](https://github.com/BrewInteractive/oauth-server/commit/50510cd7fa3df3c9836b72ca67b3aa6cb86ad838))
* **TMID-107, TMID-109:** implemented TokenService. ([948e8cf](https://github.com/BrewInteractive/oauth-server/commit/948e8cf7faea36e98d57ae1358963ae65f01bd55))
* **TMID-107,TMID-109:** implemented generateToken methods. ([ab07ec6](https://github.com/BrewInteractive/oauth-server/commit/ab07ec6eb4ee774f676abce57752d632678e038c))
* **TMID-110:** add /oauth/token ([6fcb68d](https://github.com/BrewInteractive/oauth-server/commit/6fcb68d451f366a70c8055cb226d34c1bee030bd))
* **TMID-110:** add AuthorizeControllerTest auth/token AuthorizationCode provider ([785613f](https://github.com/BrewInteractive/oauth-server/commit/785613f30bae85ed7c9aa78c70a62df1aada74a0))
* **TMID-110:** add AuthorizeControllerTest ClientCred. provider unit test ([4e5f78c](https://github.com/BrewInteractive/oauth-server/commit/4e5f78ce410e46ba39e7a877d64aaa8632053f93))
* **TMID-110:** add AuthorizeControllerTest Refresh Token provider unit test ([50113c0](https://github.com/BrewInteractive/oauth-server/commit/50113c02f3349af53c41d0181b8aea7f1a3bdc09))
* **TMID-110:** pr feedbacks ([59a74ae](https://github.com/BrewInteractive/oauth-server/commit/59a74ae091d181274048b7f38b9f556fe12c83f2))
* **TMID-110:** remove unnecessary jpa method from RefreshTokenRepository. ([123ab7c](https://github.com/BrewInteractive/oauth-server/commit/123ab7ce6eebead57993f3aa28b34db0b9ba4c06))
* **TMID-28:** add interface IBaseAuthorizeTypeProvider and abstract BaseAuthorizeTypeProvider for AuthorizeTypeProviders ([709138f](https://github.com/BrewInteractive/oauth-server/commit/709138fdb8fbbd2c109d09ce96e2ed55ad73da62))
* **TMID-28:** add interface IBaseAuthorizeTypeProvider and abstract BaseAuthorizeTypeProvider for AuthorizeTypeProviders ([44d1b6a](https://github.com/BrewInteractive/oauth-server/commit/44d1b6a37b76f5b54072839be8098decdcd53071))
* **TMID-33:** Added Validator interface. ([7022d1d](https://github.com/BrewInteractive/oauth-server/commit/7022d1d6842c1c220e0ba72f26b6824c8c65d72f))
* **TMID-33:** Renamed parameter. ([b3dd2b5](https://github.com/BrewInteractive/oauth-server/commit/b3dd2b533d9466f157f2f25ced43de4dad998268))
* **TMID-34:** Added grantList, redirectUriList. ([c4821f3](https://github.com/BrewInteractive/oauth-server/commit/c4821f306f5f3485493e44141531a07503931e97))
* **TMID-34:** Added GrantModel. ([1cfe224](https://github.com/BrewInteractive/oauth-server/commit/1cfe22491a22fc26fb0a0c4212ae704ad71e5263))
* **TMID-34:** Added RedirectUriModel. ([9f74a07](https://github.com/BrewInteractive/oauth-server/commit/9f74a0707adb472891a0d1801e44f9552045d8e2))
* **TMID-34:** Deleted unnecessary ClientController. ([ccb2591](https://github.com/BrewInteractive/oauth-server/commit/ccb25911c95b2724829c5713341c0b69a26613ac))
* **TMID-34:** Implemented ClientValidator. ([cde1605](https://github.com/BrewInteractive/oauth-server/commit/cde1605bc70711bac00acc08eea5635d0cdee5a5))
* **TMID-35:** add AuthorizeTypeProvider with its factory ([053d132](https://github.com/BrewInteractive/oauth-server/commit/053d13287c22dc838cfa4db442555e026d69dd5f))
* **TMID-35:** add service factory exception unit test cases ([da58d2e](https://github.com/BrewInteractive/oauth-server/commit/da58d2e42ee6233a477b87678382b3112b9a0bfc))
* **TMID-36:** add abstract ServiceFactory ([1b8cd9f](https://github.com/BrewInteractive/oauth-server/commit/1b8cd9fd6cb884339118577a0d128eeda947fe4e))
* **TMID-42:** add AuthorizationCode data, fixture, service and unit tests ([52b92dc](https://github.com/BrewInteractive/oauth-server/commit/52b92dc7285a80545b7368029ef5c2f1911c90ff))
* **TMID-42:** add UserCookieService interface and implementation ([f94694e](https://github.com/BrewInteractive/oauth-server/commit/f94694ee3168380aefc14e794e86d3fd6ade9e10))
* **TMID-45:** add AuthController, ([80613bb](https://github.com/BrewInteractive/oauth-server/commit/80613bbac1a01f7bcbb414fee5e7df67603aaafc))
* **TMID-45:** add clientservice impl ([c37ca2a](https://github.com/BrewInteractive/oauth-server/commit/c37ca2a1fdf03a63111a3739a40806745f40e2f0))
* **TMID-45:** add created updated date generation ([53b8e72](https://github.com/BrewInteractive/oauth-server/commit/53b8e72bd762afd656f261ada748f8e50fdf7419))
* **TMID-45:** Check via isPresent. ([ca0a7a1](https://github.com/BrewInteractive/oauth-server/commit/ca0a7a19cc080b6d900a893df0beda274d9e6dca))
* **TMID-45:** created TokenModel. ([c3a9c65](https://github.com/BrewInteractive/oauth-server/commit/c3a9c658273eabd4d2f6b7f2044ec14e350fd7ec))
* **TMID-45:** fix client id as client_id ([99003d9](https://github.com/BrewInteractive/oauth-server/commit/99003d95eaf65b2fdbe0a0188a41488b041be717))
* **TMID-45:** Fixed naming conventions. ([65fac4a](https://github.com/BrewInteractive/oauth-server/commit/65fac4ac2e5c1930fd98a3882ce347345c592c27))
* **TMID-46:** add AuthorizeRequest class ([d25a947](https://github.com/BrewInteractive/oauth-server/commit/d25a947a10fa18e4df6591d73d2cf030919737f9))
* **TMID-46:** add AuthorizeRequest class ([6d45f04](https://github.com/BrewInteractive/oauth-server/commit/6d45f045f78fe923f7b1263c19f62b4b7bf32ae0))
* **TMID-46:** clean UriUtils.isValidUrl ([10ea12a](https://github.com/BrewInteractive/oauth-server/commit/10ea12a88598f6ae0999ef5f936899e40b6a9422))
* **TMID-46:** fix url validator remove unnecessary conditions ([ce96ace](https://github.com/BrewInteractive/oauth-server/commit/ce96acef9a5c657881958a030b18e2fc2b8d9c28))
* **TMID-47-38:** add CookieServiceTest, CookieFixture ([05435ef](https://github.com/BrewInteractive/oauth-server/commit/05435ef7907dcabefca77a6e660ffd7917693028))
* **TMID-47-38:** add unit test case for empty cookie result in shouldNotGetUserCookieByKey ([67b96d5](https://github.com/BrewInteractive/oauth-server/commit/67b96d5bc621421c03d7629514571dcfba508bcf))
* **TMID-47-38:** add UserCookieService interface and implementation ([78bb8df](https://github.com/BrewInteractive/oauth-server/commit/78bb8df631b0b0f93856a5bce86b3d5b5bf9cc14))
* **TMID-47-38:** fix sonar warnings ([c5441f9](https://github.com/BrewInteractive/oauth-server/commit/c5441f900bd57fb8f18ba1866e01c762e5118386))
* **TMID-49:** add ClientRepository ([1681ce0](https://github.com/BrewInteractive/oauth-server/commit/1681ce08fa2465188400378d65b0c24ecd5d7fed))
* **TMID-49:** fix client repository, client repository test ([8bce765](https://github.com/BrewInteractive/oauth-server/commit/8bce765389134fdca31a3ad16be95df1c7e02864))
* **TMID-80:** added healthcheck endpoint. ([d272be8](https://github.com/BrewInteractive/oauth-server/commit/d272be859dbc614db7a057845d7067f0f166180f))
* **TMID-81:** remove sample components ([e029a42](https://github.com/BrewInteractive/oauth-server/commit/e029a423ee07a40720552ef625aefdff9c6bacf9))
* **TMID-83:** add BaseTokenGrantProvider.validate, BaseTokenGrantProvider.generateToken ([f65bae4](https://github.com/BrewInteractive/oauth-server/commit/f65bae428bb738d9aff08f591353df79fb9c51f5))
* **TMID-83:** add TokenGrantProviderFactory ([032cf8e](https://github.com/BrewInteractive/oauth-server/commit/032cf8e9b580f623f43a6d6c1715b63dcb6c4686))
* **TMID-83:** add TokenGrantProviderFactory ([6ad5c77](https://github.com/BrewInteractive/oauth-server/commit/6ad5c778d18c6fd87aade8feb912af1b6d6e7f8c))
* **TMID-83:** changed error message. ([f32107a](https://github.com/BrewInteractive/oauth-server/commit/f32107a6ce6e26b1bc5812e32cf76386ffc8f792))
* **TMID-84:** added @Setter. ([263b76f](https://github.com/BrewInteractive/oauth-server/commit/263b76f213c212a8c9b3fdef1656caeb09a1965d))
* **TMID-84:** created TokenRequestModel class. ([8de4445](https://github.com/BrewInteractive/oauth-server/commit/8de44457b4890cbaee8c397450a534178e55eb99))
* **TMID-95:** add ClientService.decodeClientCredentials and unit tests ([3ddc938](https://github.com/BrewInteractive/oauth-server/commit/3ddc9389a3e4f2779dd225a06e6cab6368f120f2))
* **TMID-96:** added new grant_types. ([133fcce](https://github.com/BrewInteractive/oauth-server/commit/133fcce75460bf8c6fbb35f9f6d4369ba92e55da))
* **TMID-96:** converted Validator to BaseValidator. ([01bd458](https://github.com/BrewInteractive/oauth-server/commit/01bd458c2ebb6175bcdb2781bfbf87db96edd2fb))
* **TMID-96:** refactored ClientValidator. ([b86adad](https://github.com/BrewInteractive/oauth-server/commit/b86adad9818654e05ab027b88bb94e16b80d29df))
* **TMID-96:** refactored ClientValidator. ([2c7e741](https://github.com/BrewInteractive/oauth-server/commit/2c7e741f2958ac139066e575ffc26d77ee5d492c))
* **TMID-97:** add TokenGrantProviderFactory ([135ee13](https://github.com/BrewInteractive/oauth-server/commit/135ee13254f96b1d58e3b05fd48ffd2325e2a1c4))
* **TMID-98:** add TokenGrantProviderAuthorizationCode and unit tests ([6744b64](https://github.com/BrewInteractive/oauth-server/commit/6744b641a2c36b2c47891b3cd1369bda48b57236))
* **TMID-98:** fix AuthorizationCodeServiceTest complexity ([0b758d2](https://github.com/BrewInteractive/oauth-server/commit/0b758d280e65ac5bfc60558cb8143b7aaa83f0d8))
* **TMID-98:** fix AuthorizationCodeServiceTest warning ([94449eb](https://github.com/BrewInteractive/oauth-server/commit/94449eb3185aed671a5b87665c27e9eb1c487f61))
* **TMID-98:** fix TokenGrantProvider.validate return ([98fac15](https://github.com/BrewInteractive/oauth-server/commit/98fac15fcf4d25748e4a8f07d39921cf3d1b1029))
* **TMID-99:** fix TokenGrantProviderClientCredentials and unit tests ([9998562](https://github.com/BrewInteractive/oauth-server/commit/999856276c9ac899894a910210fe8a0687b4f41f))
* **TMID-9:** add AuthorizeTypeProviderAuthorizationCode ([f2e53a8](https://github.com/BrewInteractive/oauth-server/commit/f2e53a867938b64842fd2f9cce7523213288c434))
* **TMID:104:** implemented signToken. ([3ea3d7c](https://github.com/BrewInteractive/oauth-server/commit/3ea3d7cb4b366b711bca8ed259e8fe5ee2f0b334))
* **TMID:104:** refactored with sonar warnings. ([ccbe68e](https://github.com/BrewInteractive/oauth-server/commit/ccbe68e6f691b53075a7dc06436dfdc709e78681))
* updated RefreshToken entity. ([5395629](https://github.com/BrewInteractive/oauth-server/commit/53956295792ce34121735f722d12f482906925ab))


### Bug Fixes

* add test for Application.main ([48b3d4b](https://github.com/BrewInteractive/oauth-server/commit/48b3d4b186b78b43316286554d796d7152e3aeb1))
* fix ClientValidatorTest invalid clientredirect case ([6e44ab6](https://github.com/BrewInteractive/oauth-server/commit/6e44ab602b71084bb47f260b46b68aade4d9bee8))
* **MOB-110:** fix StringUtils.generateSecureRandomString ([abd70d7](https://github.com/BrewInteractive/oauth-server/commit/abd70d784053beede8c2035d6d702aa1bc62e91d))
* refactor get client from ClientFixture ([bed3aba](https://github.com/BrewInteractive/oauth-server/commit/bed3aba27aef3c2c93e335d388ad506ed0931e05))
* removed @Data annotation from entities. ([9ae5169](https://github.com/BrewInteractive/oauth-server/commit/9ae5169839d3b18eb19b6d6499ea7aadbf20a034))
* **TMID-106:** add RefreshTokenService.revokeRefreshToken ([579bdb7](https://github.com/BrewInteractive/oauth-server/commit/579bdb7c1a559f0dd7d0cd5129a52449c44e0af4))
* **TMID-106:** fix RefreshTokenServiceImpl use active refresh tokens via db view ([1b88a40](https://github.com/BrewInteractive/oauth-server/commit/1b88a408a24bef34931c02c3a2b5a4889e59a8fb))
* **TMID-108:** add RefreshTokenService.createRefreshToken ([3f6be59](https://github.com/BrewInteractive/oauth-server/commit/3f6be595eaed085f20e1ff726e78bb58f02faac9))
* **TMID-110:** add invalid request test for auth/token ([e582920](https://github.com/BrewInteractive/oauth-server/commit/e582920ce5d26f7234b88c952731c2be0fdb26e5))
* **TMID-110:** cleanup, add error test cases ([974f9e5](https://github.com/BrewInteractive/oauth-server/commit/974f9e534d983c2a717381c0a002f9881581b3fc))
* **TMID-110:** remove null argument for TokenService.generateToken ([98bf2c8](https://github.com/BrewInteractive/oauth-server/commit/98bf2c887e8d0f9a42ee8088581e02ccadc7f8a0))
* **TMID-32:** add ClientServiceTest ([27bbfcb](https://github.com/BrewInteractive/oauth-server/commit/27bbfcbd4d6687245a37d176613bc0760d5e5f7e))
* **TMID-35:** fix service factory MissingServiceException case ([3b09530](https://github.com/BrewInteractive/oauth-server/commit/3b09530b022840b375066e64a4da70e24ab7a83c))
* **TMID-36:** fix package name authorizeType to authorizetype ([28b2bd1](https://github.com/BrewInteractive/oauth-server/commit/28b2bd1a641cfc907cf5283b239995a31e376c35))
* **TMID-36:** fix package name authorizeType to authorizetype ([a6d80fb](https://github.com/BrewInteractive/oauth-server/commit/a6d80fb86724f76f712c46cfa1d22ce913970952))
* **TMID-36:** remove incorrent provider interface ([4a8e9ff](https://github.com/BrewInteractive/oauth-server/commit/4a8e9ffc90e8c956abae9c1548490850b17673f6))
* **TMID-45:** add integration test for authorize endpoint ([686e2cb](https://github.com/BrewInteractive/oauth-server/commit/686e2cb935a63569d63060b8b2b34e4e9f00facb))
* **TMID-45:** add integrationt est UnsupportedServiceTypeException case ([2d4a554](https://github.com/BrewInteractive/oauth-server/commit/2d4a554d3d1bba8f454340dcb9d1bea9cc516b15))
* **TMID-45:** add isValidUrl method unit tests ([f3a78fe](https://github.com/BrewInteractive/oauth-server/commit/f3a78fedbbd3a2eeae478cec3a812f232922d029))
* **TMID-45:** add post request with integration tests ([2e7d05a](https://github.com/BrewInteractive/oauth-server/commit/2e7d05ab5c7bae7b92ce51764008fed7b0b39b66))
* **TMID-45:** add slf4j logger to ClientServiceImpl ([2354c8a](https://github.com/BrewInteractive/oauth-server/commit/2354c8a7a87ee5ddfb136ac8a5e9511168e1951e))
* **TMID-45:** add unit test for no client exception in AuthorizationCodeService.createAuthorizationCode ([2e9c41a](https://github.com/BrewInteractive/oauth-server/commit/2e9c41a952f7bf4fd88747b11662de8aba2c07c6))
* **TMID-45:** convert all response status as FOUND redirect ([93f1a87](https://github.com/BrewInteractive/oauth-server/commit/93f1a87aa77c73085e7f0b8846e66661ddd63fd8))
* **TMID-45:** fix AuthorizationCode save. ([907ed45](https://github.com/BrewInteractive/oauth-server/commit/907ed45b3a262825b0d4a5c6590dac6d8e85cdb8))
* **TMID-45:** fix clientMapper ([3911f75](https://github.com/BrewInteractive/oauth-server/commit/3911f75b2d3c19fc17ce27e49d7c9459ad3470a8))
* **TMID-45:** fix regex as sonar suggests ([d79c8ef](https://github.com/BrewInteractive/oauth-server/commit/d79c8ef4c741d2e6f77789aae49f9ad2c5cc9c12))
* **TMID-45:** fix regex as sonar suggests ([36a85d2](https://github.com/BrewInteractive/oauth-server/commit/36a85d29264b1d08a2024e6213078bf904670020))
* **TMID-45:** fix unit tests namings ([34d2a0e](https://github.com/BrewInteractive/oauth-server/commit/34d2a0e6e82724672de4a0eaeb0b7939f8356d5d))
* **TMID-45:** fix url validation ([9887d9c](https://github.com/BrewInteractive/oauth-server/commit/9887d9c84fc026ccdfbe64ceb1994fa69a3aca1d))
* **TMID-45:** pull request cleanup ([702f181](https://github.com/BrewInteractive/oauth-server/commit/702f181f025fab0e287735cbf166478921038847))
* **TMID-45:** remove @Data add @Getter @Setter to resolve jpa nested query conflict ([d8c64a3](https://github.com/BrewInteractive/oauth-server/commit/d8c64a32bfd0adcbdb9473e5129290113819029e))
* **TMID-45:** remove unnecessary exception ([ddc388c](https://github.com/BrewInteractive/oauth-server/commit/ddc388c6fe6cbf6a800741fafc527cb9dcf7af12))
* **TMID-45:** resolve integration test data from sql file to fixture generated. ([1f72b9a](https://github.com/BrewInteractive/oauth-server/commit/1f72b9a70ef09988fa628c9150d426167b419b74))
* **TMID-45:** revert Jacoco version downgrade ([c6e4e18](https://github.com/BrewInteractive/oauth-server/commit/c6e4e18ba17f17875f11eb8717f137826ae44718))
* **TMID-46:** rename AuthorizeRequest model ([7815d30](https://github.com/BrewInteractive/oauth-server/commit/7815d304c569878cee6543a9672fd9ad52e8de3c))
* **TMID-83:** fix BaseTokenGrantProvider.validate ([e636a9a](https://github.com/BrewInteractive/oauth-server/commit/e636a9a4189bfc3914a7fe9bad94069ffd40dee7))
* **TMID-83:** fix BaseTokenGrantProvider.validate ([14fd949](https://github.com/BrewInteractive/oauth-server/commit/14fd9491926a33b0f5c4f4c0ce0b89d42f3938f7))
* **TMID-83:** fix return model of BaseTokenGrantProvider.generateToken ([8e2b408](https://github.com/BrewInteractive/oauth-server/commit/8e2b4081632c2fae707940476c1b40e51ded56b2))
* **TMID-83:** fix return model of BaseTokenGrantProvider.generateToken ([d5db1d0](https://github.com/BrewInteractive/oauth-server/commit/d5db1d0ed331f9f2c1cfedff0cd971fb7b2e6bba))
* **TMID-83:** fix TokenGrantProviderRefreshToken ([57f3197](https://github.com/BrewInteractive/oauth-server/commit/57f31976e6204ab31164dfd6a249f31d8233f871))
* **TMID-9:** fix module names for regex rules sonar code smells ([4af0b54](https://github.com/BrewInteractive/oauth-server/commit/4af0b54a5d58ce6cba992a767307bd140c54a3de))
* update spring-boot-starter-parent ([94736c1](https://github.com/BrewInteractive/oauth-server/commit/94736c1d8ce44811f06dfd89b9377a55e8f32150))

## [1.1.0](https://github.com/BrewInteractive/oauth-server/compare/v1.0.0...v1.1.0) (2023-03-08)


### Features

* **TMID-39:** fix test folder and package names ([0fa3a5d](https://github.com/BrewInteractive/oauth-server/commit/0fa3a5dc6c31659e123c365c93aab6963e86af62))
* **TMID-8:** Added EnableAutoConfiguration. ([74fcf3f](https://github.com/BrewInteractive/oauth-server/commit/74fcf3f0e4ca0664cd9756bfc1de8998abc32ac3))
* **TMID-8:** Removed spring.config.import. ([85c244a](https://github.com/BrewInteractive/oauth-server/commit/85c244a9f6367057d4eb6c0b36d758cd351aab34))


### Bug Fixes

* **TMID-40:** remove unnecessary jwt service ([21816ff](https://github.com/BrewInteractive/oauth-server/commit/21816ff919be62ba8aeb0664e65aea2827219bd7))
* **TMID-41:** add AuthorizationCodeService interface ([3daa00f](https://github.com/BrewInteractive/oauth-server/commit/3daa00fe638ef7b68ffca2b7c98987cbdb650be7))
* **TMID-41:** fix service method names as camel case ([cf98ff2](https://github.com/BrewInteractive/oauth-server/commit/cf98ff282c28d22d1069d0193d2b1db63b74e3b6))

## 1.0.0 (2023-03-06)


### Features

* **TMID-3,TMID-5,TMID-7:** add JwtService, TokenService, GenerateRandomTokenString ([6cf5ecf](https://github.com/BrewInteractive/oauth-server/commit/6cf5ecfe0261fc0cfe716fa81abc002bdf754f4e))
* **TMID-4:** Added db tables. ([1085fa0](https://github.com/BrewInteractive/oauth-server/commit/1085fa07ddc7243bc68f9bef41177e8b4acd385b))
* **TMID-6:** Created project. ([1d2c558](https://github.com/BrewInteractive/oauth-server/commit/1d2c5585b249f40208a01d368d70888609a1e5d6))
* **TMID-8:** Added Dockerfile. ([0714b57](https://github.com/BrewInteractive/oauth-server/commit/0714b570bebe0e8049aacc5ec549ea63d55126fb))
* **TMID-8:** Added release script. ([bd48fec](https://github.com/BrewInteractive/oauth-server/commit/bd48fecc16fb74f1e35431ede5879a0bcdaceabd))
* **TMID-8:** Installed standard-version. ([05491e5](https://github.com/BrewInteractive/oauth-server/commit/05491e50d92c138f42a41bd39f7e0cddbf6c1c3f))
* **TMID-8:** Updated relaese action. ([61a8a9c](https://github.com/BrewInteractive/oauth-server/commit/61a8a9cbb4de64acea9642ec67466b4ceec148a8))


### Bug Fixes

* **TMID-3,TMID-5,TMID-7:** remove unnecessary throws ([e1a3b37](https://github.com/BrewInteractive/oauth-server/commit/e1a3b37811453dca5b633069d3a58e7114ae3daa))
