# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

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
