# web-parser-rest

A server provide restful API to parse resources from websites.

## API to get contents
> We added swagger support to the server. Full list of API can be found from /swagger-ui.html after starting the server.

| Type       | API                                                                  | Parameters                                                                                               | Contents                                                                              |
|:-----------|:---------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------|
| Search     | GET `/rest/search/image/{query}`                                     | **query**: string to search                                                                              | Search Image from seach engine <br>*(Google api key may needed)*                      |
| Finance    | GET `/rest/quote/indexes`                                            |                                                                                                          | Real time quote of major indexes in the world                                         |
| Finance    | GET `/rest/quote/full/{code}?source=<money18,aastock>`               | **code**: Hong Kong Stock code e.g. 0005 <br>**source (optional)**: specific source to get stock quote   | Real time quote of HK stocks with PE and NAV                                          |
| Finance    | GET `/rest/quote/realtime/list/{codes}?source=<money18,aastock>`     | **codes**: comma separated  HK stock codes <br>**source (optional)**: specific source to get stock quote | Real time quote of HK stocks (faster)                                                 |
| Finance    | GET `/rest/index/constituents/{index}`                               | **index**: HSI / HSCEI / HCCI / MSCIHK                                                                   | Constituents of major HK Indexes                                                      |
| Finance    | GET `/rest/index/report/hsinet/{yyyymmdd}`                           | **yyyymmdd**: date of report                                                                             | [Hang Seng Index Performance Summary ](https://www.hsi.com.hk)                        |
| Finance    | GET `/rest/quote/{code}/range/{fromDate:yyyymmdd}/{toDate:yyyymmdd}` | **code**: stock codes <br> **mic code**: XHKG / XNAS / etc                                               | Get historical quotes                                                                 |
| Dictionary | GET `/rest/dictionary/{query}`                                       | **query**: word to query                                                                                 | Get pronunciation, IPA, definition from Cambridge Dictionary API or Dictionaryapi.com |
| Dictionary | GET `/rest/dictionary/google/{query}`                                | **query**: word to query                                                                                 | Get vocabulary meaning from google                                                    |

## Starting server
### Build and start by Gradle
```bash
./gradlew assemble (for windows ./gradlew.bat assemble)
java -jar build/libs/web-parser-rest.jar 
```
access http://localhost:8080/swagger-ui.html

### Start by Docker
#### Simple start
```docker run -d --name web-parser-rest thcathy/web-parser-rest```

#### Start with different port
```
docker run -d --name web-parser-rest \
  -p <port_you_want>:8080 \
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
* `googleapi.key`: Google API key for image search (support multiple keys separated by comma)
* `dictionaryapi.key` : dictionaryapi.com app key (get key from https://dictionaryapi.com)

### Pass configurations by java command
e.g. `java -jar build/libs/web-parser-rest.jar -D<key>=<value>`

### Pass configurations by docker env
e.g. `docker run -d --name web-parser-rest -e googleapi.key=xxxxxx thcathy/web-parser-rest`

## Disclaimer

**This is intended for academic purposes. The copyright of contents parsed remain owned by the source websites.
Use it at your own risk.**

## Major Dependency
* [Spring boot](http://projects.spring.io/spring-boot/): application framework
* [Spring webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html): web server
* [Async Http Client](https://github.com/AsyncHttpClient/async-http-client): http client
* [Jsoup](https://jsoup.org/): HTML Parser
