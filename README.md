# Click_AccountBook

명지대학교 2023년도 1학기 &lt;모바일컴퓨팅> 기말 프로젝트

## 주제 : 영수증 인식을 통한 가계부 자동 작성

- **App 이름** : 찰칵 가계부 (Click Account Book)
- 사용할 API : NAVER CLOVA OCR API (https://api.ncloud-docs.com/docs/ai-application-service-ocr-ocrdocumentocr)
- 기능
  - `AddReceiptActivity`: 갤러리에서 영수증 이미지를 선택하고, 이를 `DatabaseHandler` 클래스에 전달. 이후 OCR API를 통해 정보를 추출하는 기능
  - `DatabaseHandler`: OCR API를 통해 추출한 영수증 정보 및 선택한 사진 정보를 데이터베이스에 저장하는 기능
  - `GalleryActivity`: 저장된 영수증 정보를 갤러리에서 보여주는 기능
  - `StatisticsActivity`: 영수증 정보를 주/월별로 정리하여 소비 패턴을 그래프로 보여주는 기능
  - `SortReceiptsActivity`: 영수증을 금액별로 정렬하여 보여주는 기능
  - `FilterReceiptsActivity`: 영수증을 항목(매장 이름, 카드 번호 등)별로 필터링하여 보여주는 기능

## 테이블 정의서

1. **Receipt Table**

   | Column Name        | Data Type | Nullable | Description                                       |
   | ------------------ | --------- | -------- | ------------------------------------------------- |
   | id                 | String    | No       | Primary Key                                       |
   | storeName          | String    | No       | The name of the store where the purchase was made |
   | storeSubName       | String    | Yes      | The sub name of the store                         |
   | storeBizNum        | String    | Yes      | The business number of the store                  |
   | storeAddress       | String    | Yes      | The address of the store                          |
   | storeTel           | String    | Yes      | The telephone number of the store                 |
   | paymentDate        | Date      | No       | The date when the payment was made                |
   | paymentTime        | Date      | No       | The time when the payment was made                |
   | paymentCardCompany | String    | Yes      | The card company used for the payment             |
   | paymentCardNumber  | String    | Yes      | The card number used for the payment              |
   | paymentConfirmNum  | String    | Yes      | The confirmation number of the payment            |
   | totalPrice         | Float     | No       | The total price of the items purchased            |
   | estimatedLanguage  | String    | No       | The language estimated by OCR API                 |

2. **ImageData Table**

   | Column Name | Data Type | Nullable | Description                          |
   | ----------- | --------- | -------- | ------------------------------------ |
   | id          | String    | No       | Primary Key                          |
   | format      | String    | No       | The format of the image              |
   | data        | ByteArray | No       | The actual image data                |
   | timestamp   | Date      | No       | The time when the image was captured |
   | receiptId   | String    | No       | Foreign Key referencing Receipt.id   |

3. **ItemData Table**

   | Column Name   | Data Type | Nullable | Description                           |
   | ------------- | --------- | -------- | ------------------------------------- |
   | id            | String    | No       | Primary Key                           |
   | receiptId     | String    | No       | Foreign Key referencing Receipt.id    |
   | itemName      | String    | No       | The name of the item                  |
   | itemCode      | String    | Yes      | The code of the item                  |
   | itemCount     | Float     | Yes      | The count of the item purchased       |
   | itemPrice     | Float     | Yes      | The total price of the item purchased |
   | itemUnitPrice | Float     | Yes      | The unit price of the item            |



## ERD

```lua
   +-------------+     +--------------+    +-------------+
   |   Receipt   |     |   ImageData  |    |   ItemData  |
   +-------------+     +--------------+    +-------------+
   | id (PK)     |     | id (PK)      |    | id (PK)     |
   | storeName   |<-|--| receiptId (FK)|<--| receiptId (FK)|
   | storeSubName|     | format       |    | itemName    |
   | storeBizNum |     | data         |    | itemCode    |
   | storeAddress|     | timestamp    |    | itemCount   |
   | storeTel    |     +--------------+    | itemPrice   |
   | paymentDate |                         | itemUnitPrice|
   | paymentTime |                         +--------------+
   | paymentCardCompany |
   | paymentCardNumber  |
   | paymentConfirmNum  |
   | totalPrice         |
   | estimatedLanguage  |
   +--------------------+

```



## 클래스 정의서

1. `MainActivity` 클래스: 앱을 실행했을 때 첫 화면을 담당. Intent 를 사용해서 여러 화면에 기능들을 보여줌.
2. `Receipt` 클래스: 영수증 정보를 저장하는 클래스. OCR API를 통해 얻은 정보를 이 클래스의 인스턴스로 저장.
3. `ReceiptImage` 클래스: 갤러리에서 선택한 영수증 이미지와 관련된 정보를 저장하는 클래스.
4. `ReceiptOCR` 클래스: OCR API를 호출하는 메소드를 포함하는 클래스.
5. `DatabaseHandler` 클래스: SQLite 데이터베이스와 상호작용하는 클래스. 영수증 정보를 저장하고 불러오는 기능을 담당.
6. `GalleryActivity` 클래스: 사용자가 저장한 영수증 이미지들을 보여주는 화면을 담당하는 클래스.
7. `AddReceiptActivity` 클래스: 사용자가 새로운 영수증을 추가할 때 사용하는 화면을 담당하는 클래스. 갤러리에서 이미지를 선택하고, OCR API를 호출하는 기능을 포함.
8. `StatisticsActivity` 클래스: 사용자의 주/월별 소비 그래프를 보여주는 화면을 담당하는 클래스.
9. `SortReceiptsActivity` 클래스: 영수증을 금액별로 정렬해서 보여주는 화면을 담당하는 클래스.
10. `FilterReceiptsActivity` 클래스: 영수증을 항목(매장 이름, 카드 번호 등)별로 필터링해서 보여주는 화면을 담당하는 클래스.



## 동작 과정

**사용자 관점에서의 동작 과정:**

1. 사용자는 앱을 실행합니다. (`MainActivity`가 실행됨)
2. `MainActivity`에서 사용자는 하단에 있는 탭을 클릭하여 각 탭에 해당하는 기능을 사용할 수 있습니다. (AddReceiptActivity, GalleryActivity,StatisticsActivity,SortReceiptsActivity,FilterReceiptsActivity)
3. 사용자가 새로운 영수증을 추가하고자 할 경우, "AddReceiptActivity' 탭으로 이동하여 '영수증 추가' 버튼을 클릭합니다.
4. 버튼을 클릭하면 화면은 갤러리로 이동되고 사용자는 추가하고 싶은 이미지를 선택합니다.
5. 선택된 이미지는  Image table 에 저장되고 GalleryActivity에서 확인할 수 있습니다.
6. 그 후 선택된 이미지는 `ReceiptOCR` 클래스에 의해 OCR API로 전송되어 텍스트 정보를 추출합니다.
7. 추출된 정보는 `Receipt` 인스턴스로 저장되고, `DatabaseHandler`를 통해 SQLite 데이터베이스에 저장됩니다.
8. 사용자는 `StatisticsActivity`에서 주/월별 소비 그래프를 확인할 수 있습니다.
9. 사용자는 `SortReceiptsActivity`에서 금액별로 정렬된 영수증을 확인할 수 있습니다.
10. 사용자는 `FilterReceiptsActivity`에서 항목별로 필터링된 영수증을 확인할 수 있습니다.



**클래스 및 DB 관점에서의 동작 과정:**

1. `MainActivity`가 실행되면, `DatabaseHandler`를 통해 SQLite 데이터베이스에서 영수증 데이터를 불러옵니다.
2. 데이터베이스에 저장된 영수증 정보들은 `GalleryActivity`에 전달되어 갤러리에 표시됩니다.
3. 사용자가 영수증을 추가하면, `AddReceiptActivity`에서 `DatabaseHandler` 를 통해 SQLite 데이터베이스에 저장됩니다. 또한 사용자가 선택한 이미지를 `ReceiptOCR`에 전달합니다.
4. `ReceiptOCR` 클래스는 OCR API를 호출하여 이미지에서 텍스트 정보를 추출합니다.
5. 추출된 정보는 `Receipt` 인스턴스로 만들어져 `DatabaseHandler`를 통해 SQLite 데이터베이스에 저장됩니다.
6. `StatisticsActivity` 클래스는 `DatabaseHandler`를 통해 데이터베이스에서 영수증 데이터를 불러와 주/월별 소비 그래프를 생성합니다.
7. `SortReceiptsActivity` 클래스는 `DatabaseHandler`를 통해 데이터베이스에서 영수증 데이터를 불러와 금액별로 정렬합니다.
8. `FilterReceiptsActivity` 클래스는 `DatabaseHandler`를 통해 데이터베이스에서 영수증 데이터를 불러와 사용자가 선택한 항목에 따라 필터링합니다.

# 프로젝트 규칙  

- 코드, 폴더 구조 변경 금지  

- Commit 규칙 준수하기  

- Commit은 따로 Branch 생성 / Fork 후 PR하기  

- 커밋 메시지는 "[Type] message" 형식으로 작성하기  

  | **Type** | **Meaning**                   |
  | -------- | ----------------------------- |
  | Feat     | 새로운 기능                   |
  | Fix      | 버그 수정                     |
  | Resolve  | 충돌 해결                     |
  | Build    | 빌드 관련 파일 수정           |
  | Chore    | 그 외 자잘한 수정             |
  | Style    | 스타일 변경                   |
  | Docs     | 문서 수정                     |
  | Refactor | 코드 리팩토링                 |
  | Test     | 테스트 코드 수정              |
  | Add      | 새로운 라이브러리 / 파일 생성 |

- 

- Commit 규칙 준수하기  

- Commit은 따로 Branch 생성 / Fork 후 PR하기  

- 커밋 메시지는 "[Type] message" 형식으로 작성하기  

  |      |      |
  | ---- | ---- |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
  |      |      |
