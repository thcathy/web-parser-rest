# web-parser-rest [![CircleCI](https://circleci.com/gh/thcathy/web-parser-rest.svg?style=svg)](https://circleci.com/gh/thcathy/web-parser-rest)

A server provide restful API to parse resources from websites.

## API to get contents
> We added swagger support to the server. Full list of API can be found from /swagger-ui.html after starting the server.

Type | API | Parameters | Contents
:--- | :--- |:--- |:--- 
Search | GET `/rest/search/image/{query}` | **query**: string to search | Search Image from seach engine <br>*(Google api key may needed)*
Finance | GET `/rest/quote/indexes` | | Real time quote of major indexes in HK and China
Finance | GET `/rest/quote/full/{code}` | **code**: Hong Kong Stock code e.g. 0005 | Real time quote of HK stocks with PE and NAV
Finance | GET `/rest/quote/realtime/list/{codes}` | **codes**: comma separated  HK stock codes | Real time quote of HK stocks (faster)
Finance | GET `/rest/hkma/report/{yyyymmdd}` | **yyyymmdd**: date of report | [HKMA Monetary Base Report](http://www.hkma.gov.hk/eng/market-data-and-statistics/monetary-statistics/)
Finance | GET `/rest/index/constituents/{index}` | **index**: HSI / HSCEI / HCCI / MSCIChina / MSCIHK | Constituents of major HK Indexes
Finance | GET `/rest/index/report/hsinet/{yyyymmdd}` | **yyyymmdd**: date of report | [Hang Seng Index Performance Summary ](https://www.hsi.com.hk)
Forum | GET `/rest/forum/list/{type}/{batch}` | **type**: MUSIC / MOVIE <br> **batch**: int from 1 | Music / Movie from popular Hong Kong Forums<br>*(Forum account may needed)*
Dictionary | GET `/rest/dictionary/{query}` | **query**: word to query | Get pronunciation, IPA, definition from Longman Dictionary API

## Starting server
### Build and start by Gradle
```bash
./gradlew assemble (for windows ./gradlew.bat assemble)
java -jar build/libs/web-parser-rest.jar 
```
access http://localhost:8091/swagger-ui.html

### Start by Docker
#### Simple start
```docker run -d --name web-parser-rest thcathy/web-parser-rest```

#### Start with different port
```
docker run -d --name web-parser-rest \
  -p <port_you_want>:8091 \
  thcathy/web-parser-rest
```

#### Start with log redirection
```
docker run -d --name web-parser-rest \
  -v <folder_in_local_drive>:logs \
  thcathy/web-parser-rest
```

## Configuration
**All configurations are optional.**
* `discuss.username / discuss.password`: Account access www.discuss.com.hk
* `tvboxnow.username / tvboxnow.password`: Account access www.tvboxnow.com
* `googleapi.key`: Google API key for image search
* `pearsonapi.key` : Pearson API key for Longman dictionary API (get key from https://developer.pearson.com/user/register)
* `http.max_connection`: Max concurrent http connections to parse contents (default to 20)
* `http.max_connection_per_route`: Max concurrent connections per router (default to 20)

### Pass configurations by java command
e.g. `java -jar build/libs/web-parser-rest.jar -Dhttp.max_connection=10`

### Pass configurations by docker env
e.g. `docker run -d --name web-parser-rest -e discuss.username=**** -e discuss.password=**** thcathy/web-parser-rest`

## Disclaimer

**This is intended for academic purposes. The copyright of contents parsed remain owned by the source websites.
Use it at your own risk.**

## Major Dependency
* [Spring boot](http://projects.spring.io/spring-boot/): application framework
* [Unirest for Java](http://unirest.io/java.html): http client
* [Jsoup](https://jsoup.org/): HTML Parser
* [Springfox](http://springfox.github.io/springfox/): JSON API documentation
