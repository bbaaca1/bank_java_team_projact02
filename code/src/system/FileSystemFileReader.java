package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileSystemFileReader {
	
	final static Logger logger = LogManager.getLogger(FileSystemDataUpdater.class);

	private int accountIndex; // 계좌의 인덱스 번호를 담아둘 변수
	private String name; // 예금주명를 담아둘 변수
	private String accountNumber; // 계좌번호를 담아둘 변수
	private String ssn; // 주민번호를 담아둘 변수
	private String homeAddress; // 예금주의 집주소를 담아둘 변수
	private String phoneNumber;  // 예금주의 전화번호를 담아둘 변수
	private long balance; // 잔액을 담아둘 변수
	private String accountType; // 계좌 유형을 담아둘 변수
	private Date cellDate = new Date(); // 시간 데이터타입변환(Date -> LocalDate)를 위한 객체 선언 
	private LocalDate lastTradingDate; // 마지막거래일자를 담아둘 변수
	private LocalDate dueDate; // 납기일을 담아둘 변수
	private LocalDate maturityDate; // 만기일을 담아둘 변수
	private String accountPasswd; // 계좌 비밀번호를 담아둘 변수
	private int state = 0; // 계좌 상태를 담아둘 변수
	private int creditScore = 0; // 신용점수를 담아둘 변수
	private String vipRank; // 고객등급을 담아둘 변수
	
	//파일및 폴더 경로용 상수 변수들
	private static final String ACCOUNT_XLSX_FILE_PATH = "file_system\\user_data\\accounts.xlsx"; // 계좌 정보를 가지고있는 엑셀 파일의 상대경로
	private static final String USER_LEMIT_XLSX_FILE_PATH = "file_system\\user_data\\user_limit.xlsx"; // 일반 계좌의 제한정보를 가지고 있는 엑셀 파일의 상대경로
	private static final String USER_LOAN_XLSX_FILE_PATH = "file_system\\user_data\\user_loan.xlsx"; // 일반 계좌의 대출정보를 가지고 있는 엑셀 파일의 상대경로
	private static final File FILE_SYSTEM_PATH = new File("file_system"); // 폴더인 File_System의 상대경로
	private static final File USER_DATA_PATH = new File("file_System\\user_data"); // 폴더인 File_System안의 user_date 상대경로
	
	public void readAccountsDataFromExcel(AccountStorage as) {
		
		logger.info("Loading data");
		
		final FileInputStream fileInputStream; // 계좌 정보가 들어있는 엑셀파일의 경로를 지정하는 변수 
		Workbook excelFile = null; //
		
		Sheet depositAccountSheet = null; // 엑셀의 일반 예금 시트 변수
		Sheet timeAccountSheet = null; // 엑셀의 정기 예금 시트 변수
		Sheet savingAccountSheet = null; // 엑셀의 정기 적금 시트 변수
		Sheet checkAccountSheet = null; // 엑셀의 자유 적금 시트 변수
				
		try {
		fileInputStream = new FileInputStream(ACCOUNT_XLSX_FILE_PATH); // 상대 경로 프로젝트폴더 안의 file_system폴더안 user_date폴더에 accounts.xlsx엑셀 파일을 불러옵니다.
		excelFile = WorkbookFactory.create(fileInputStream);
		
		depositAccountSheet = excelFile.getSheetAt(0);
		timeAccountSheet = excelFile.getSheetAt(1);
		savingAccountSheet = excelFile.getSheetAt(2);
		checkAccountSheet = excelFile.getSheetAt(3);
		
		}catch (FileNotFoundException e) {
			// 파일이 없을때 실행
			createUserExcelFile(as);
			return;
			
		} catch (Exception e) {
			// 그외 에러 발생시 실행
			logger.info("data Load Failed");
			logger.fatal(e.toString());
			System.exit(1);
		}
		
		readDepositAccountData(depositAccountSheet, as);
		readTimeAccountData(timeAccountSheet, as);
		readSavingAccountData(savingAccountSheet,as);
		readFreeAccountData(checkAccountSheet,as);
		readLimitTime(as); // 동결시간, 이체제한,출금제한을 불러옴
		readLoanTime(as); // 대출정보들을 불러옴
		try {
			excelFile.close();// 워크북 닫기
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		logger.info("data Load end");	
		return;
	}
	
	// 일반 예금 계좌 시트의 정보를 읽어오는 메소드
	private void readDepositAccountData(Sheet depositAccsheet, AccountStorage as) {

		// 저장된 계좌 입력 반복문
		// 1번째 행은 표제목이므로 사용하지않음
		accountIndex = 0;
		while(true) {
			Row row = depositAccsheet.getRow(accountIndex+1); // i번째 행 i는 2번째(i=1) 행부터 사용합니다.
			if (row != null) {
				for (Cell cell : row) {
					switch (cell.getColumnIndex()) {
					case 0:
						// 이름 입력 : 1열
						name = cell.getStringCellValue();
						break;
					case 1:
						// 주민 번호 입력 : 2열
						ssn = cell.getStringCellValue().replace("-", "");// 주민번호 사이의 -(하이픈)을 제거함
						break;

					case 2:
						// 계좌 번호 입력 : 3열
						accountNumber = cell.getStringCellValue().replace("-", ""); // 계좌번호 사이의 -(하이픈)을 제거함
						break;
					case 3:
						// 비밀번호 : 4열
						accountPasswd = cell.getStringCellValue();
						break;

					case 4:
						// 잔액 입력 : 5열(숫자)
						balance = (long) cell.getNumericCellValue();
						break;
					case 5:
						// 마지막거래일자 : 6열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						lastTradingDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
					case 6:
						// 주소 입력 : 7열
						homeAddress = cell.getStringCellValue();
						break;

					case 7:
						// 전화 번호 입력 : 8열
						phoneNumber = cell.getStringCellValue().replace("-", "");// 전화번호 사이의 -(하이픈)을 제거함
						break;	
					case 8:
						// 계좌 상태 : 9열
						state = (int) cell.getNumericCellValue();
						break;
					case 9:
						// 신용 점수 : 10열
						creditScore = (int)cell.getNumericCellValue();
						break;
					case 10:
						// 우대고객여부 : 11열
						vipRank = cell.getStringCellValue();
						break;
				}
	
			}
				
			/*
			 * 엑셀에서 불러온 정보를 가진 객체를 생성해 list에 할당
			 * 계좌번호와 계좌비밀번호를 키와 인덱스 번호값을 가진 멀티맵을 생성
			 * 계좌번호를 키와 인덱스 번호값을 가진 멀티맵을 생성
			 * 
			 */
		
			// 리스트에 추가(객체 생성) 및 multKeyMap과 Map 설정
			accountType = "일반 예금";
			as.addDepositAccount(new Account(name, accountNumber, ssn, homeAddress, phoneNumber, balance, accountType, lastTradingDate, accountPasswd, state, creditScore, vipRank)); 
			as.getDepositAccount(accountIndex).setDepositInterestRate(0.1);
			as.putMultikeyMap(accountNumber, accountPasswd, accountIndex); 
			as.putMap(accountNumber, accountIndex);
				
			
			/*	휴면계좌 설정
			 *  잔액이 1만원 미만 1년 
			 *  잔액이 1만원이상 5만원 미만 2년 
			 *  잔액이 5만원 이상 10만원 미만은 3년 
			 *  그 이상은 휴면 없음
			 */
			if (as.getDepositAccount(accountIndex).getState() != 2) { // 이미 휴면 계좌 일경우 이 조건문을 건너뜀
				LocalDate datePlusYear = null; // 저장된 마지막 거래일자에 1,2,3년을 더할 변수
				LocalDate now = LocalDate.now(); // 현재 시간을 담는 변수
				if (balance < 10000) {
					// 계좌의 잔액이 1만원 미만이면 실행됩니다.
					datePlusYear = lastTradingDate.plusYears(1); // 변수의 값을 최종 거래일자에서 1년후로 설정				
					if (now.compareTo(datePlusYear) >= 0) {
						// 만약 저장된 날짜와 오늘 날짜와 비교하여 값의 차이가 0이상(0은 당일)이면 실행됩니다.
						as.getDepositAccount(accountIndex).setNonTradingAcc(true); // 휴면계좌 
						as.getDepositAccount(accountIndex).setState(2);
						state = 2;
					}
				} else if (balance < 50000) {
					// 계좌의 잔액이 5만원 미만이면 실행됩니다.
					datePlusYear = lastTradingDate.plusYears(2); // 변수의 값을 최종 거래일자에서 2년후로 설정
					
					if (now.compareTo(datePlusYear) >= 0) {
						// 만약 저장된 날짜와 오늘 날짜와 비교하여 값의 차이가 0이상(0은 당일)이면 실행됩니다.
						as.getDepositAccount(accountIndex).setNonTradingAcc(true);
						as.getDepositAccount(accountIndex).setState(2);
						state = 2;
					}
					
				} else if (balance < 100000) {
					// 계좌의 잔액이 10만원 미만이면 실행됩니다.
					datePlusYear = lastTradingDate.plusYears(3); // 변수의 값을 최종 거래일자에서 3년후로 설정
					
					if (now.compareTo(datePlusYear) >= 0) {
						// 만약 저장된 날짜와 오늘 날짜와 비교하여 값의 차이가 0이상(0은 당일)이면 실행됩니다.
						as.getDepositAccount(accountIndex).setNonTradingAcc(true);
						as.getDepositAccount(accountIndex).setState(2);
						state = 2;
					}
				}
			}
			as.getDepositAccount(accountIndex).setAccountStatus(state); // 계좌 상태설정
			accountIndex++; // 계좌 인덱스 번호 숫자 증가
			} else {
				break;
			}
		}
	}
	
	// 정기 예금 계좌 시트의 정보를 읽어오는 메소드
	private void readTimeAccountData(Sheet timeAccSheet, AccountStorage as) {
		accountIndex = 0;
		
		while(true) {
			Row row = timeAccSheet.getRow(accountIndex+1); // i번째 행 i는 2번째(i=1) 행부터 사용합니다.
			if (row != null) {
				for (Cell cell : row) {
					
					switch (cell.getColumnIndex()) {
					case 0:
						// 이름 입력 : 1열
						name = cell.getStringCellValue();
						break;
					case 1:
						// 주민 번호 입력 : 2열
						ssn = cell.getStringCellValue().replace("-", "");// 주민번호 사이의 -(하이픈)을 제거
						break;
	
					case 2:
						// 계좌 번호 입력 : 3열
						accountNumber = cell.getStringCellValue().replace("-", ""); // 계좌번호 사이의 -(하이픈)을 제거
						break;
					case 3:
						// 비밀번호 : 3열
						accountPasswd = cell.getStringCellValue();
						break;
					case 4:
						// 잔액 입력 : 5열(숫자)
						balance = (long) cell.getNumericCellValue();
						break;
					case 5:
						//납기일 : 6열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						dueDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 6:
						//만기일 : 7열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						maturityDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 7:
						// 마지막입금날자 : 8열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						lastTradingDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					}
				}
				accountType = "정기 예금";
				as.addTimeAccount(new Account(name, accountNumber, ssn, 
						accountPasswd, balance, accountType, 
						lastTradingDate, maturityDate, dueDate));
				as.getTimeAccount(accountIndex).setTimeInterestRate(2.4);
				as.putMultikeyMap(accountNumber, accountPasswd, accountIndex); 
				as.putMap(accountNumber, accountIndex);
				accountIndex++;
				
			} else {
				break;
			}
		} 
	}
	
	// 정기 적금 계좌 시트의 정보를 읽어오는 메소드
	private void readSavingAccountData(Sheet savingAccsSheet, AccountStorage as) {
		accountIndex = 0;
		while(true) {
			Row row = savingAccsSheet.getRow(accountIndex+1); // i번째 행 i는 2번째(i=1) 행부터 사용합니다.
			if (row != null) {
				for (Cell cell : row) {
					
					switch (cell.getColumnIndex()) {
					case 0:
						// 이름 입력 : 1열
						name = cell.getStringCellValue();
						break;
					case 1:
						// 주민 번호 입력 : 2열
						ssn = cell.getStringCellValue().replace("-", "");// 주민번호 사이의 -(하이픈)을 제거
						break;
	
					case 2:
						// 계좌 번호 입력 : 3열
						accountNumber = cell.getStringCellValue().replace("-", ""); // 계좌번호 사이의 -(하이픈)을 제거
						break;
					case 3:
						// 비밀번호 : 3열
						accountPasswd = cell.getStringCellValue();
						break;
					case 4:
						// 잔액 입력 : 5열(숫자)
						balance = (long) cell.getNumericCellValue();
						break;
					case 5:
						//납기일 : 6열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						dueDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 6:
						//만기일 : 7열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						maturityDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 7:
						// 마지막입금날자 : 8열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						lastTradingDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					}
				}
				accountType = "정기 적금";			
				as.addSavingAccount(new Account(name, accountNumber, ssn, 
						accountPasswd, balance, accountType, 
						lastTradingDate, maturityDate, dueDate)); 
				as.getSavingAccount(accountIndex).setSavingInterestRate(3.1);
				as.putMultikeyMap(accountNumber, accountPasswd, accountIndex); 
				as.putMap(accountNumber, accountIndex);
				accountIndex++;
			} else {
				break;
			}
		}
	}
	
	// 자유 적금 계좌 시트의 정보를 읽어오는 메소드
	private void readFreeAccountData(Sheet checkAccSheet, AccountStorage as) {
		accountIndex = 0;
		while(true) {
			Row row = checkAccSheet.getRow(accountIndex+1); // i번째 행 i는 2번째(i=1) 행부터 사용합니다.
			if (row != null) {
				for (Cell cell : row) {
					switch (cell.getColumnIndex()) {
					case 0:
						// 이름 입력 : 1열
						name = cell.getStringCellValue();
						break;
					case 1:
						// 주민 번호 입력 : 2열
						ssn = cell.getStringCellValue().replace("-", "");// 주민번호 사이의 -(하이픈)을 제거
						break;
	
					case 2:
						// 계좌 번호 입력 : 3열
						accountNumber = cell.getStringCellValue().replace("-", ""); // 계좌번호 사이의 -(하이픈)을 제거
						break;
					case 3:
						// 비밀번호 : 4열
						accountPasswd = cell.getStringCellValue();
						break;
					case 4:
						// 잔액 입력 : 5열(숫자)
						balance = (long) cell.getNumericCellValue();
						break;
					case 5:
						//납기일 : 6열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						dueDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 6:
						//만기일 : 7열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						maturityDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					case 7:
						// 마지막입금날자 : 8열(날짜 데이터)
						cellDate = cell.getDateCellValue();
						lastTradingDate = cellDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // date타입으로 저장된 엑셀의 거래일자를 LocalDate타입으로 변경함
						break;
						
					}
				}
				accountType = "자유 적금";			
				as.addFreeAccount(new Account(name, accountNumber, ssn, 
						accountPasswd, balance, accountType, 
						lastTradingDate, maturityDate, dueDate)); 
				as.getFreeAccount(accountIndex).setFreeInterestRate(3.3);
				as.putMultikeyMap(accountNumber, accountPasswd, accountIndex); 
				as.putMap(accountNumber, accountIndex); 
				accountIndex++;
				} else {
					break;
				}
			}
		}

	/*
	 * 메소드 : createUserExcelFile()
	 * 파라미터 : 없음(None)
	 * 반환값 : 없음(void)
	 * 기능설명 :	만약 엑셀파일이나 저장 폴더가 없을때 실행되는 메소드 경로를 상대 경로(File_System\\user_date)로 지정하여 엑셀파일을 생성한다
	 * 			만약 엑셀파일을 담아둘 File_System이나 user_date폴더가 없다면 해당 폴더를 생성한다
	 * 			
	 */
	
	private void createUserExcelFile(AccountStorage as) {
		
		//엑셀에 입력할 표제목을 담아둘 배열(상수)
		final String[]  ACCOUNT_EXCEL_LABELS = {"이름","주민번호","계좌번호","비밀번호","잔액","마지막거래일자","주소", "전화번호", "계좌상태", "신용점수","우대고객여부"};
		final String[]  ACCOUNT_SAVING_EXCEL_LABELS = {"이름","주민번호","계좌번호","비밀번호","잔액","납기일","만기일","마지막거래일자"};
		Workbook excelFile = null;
		
        try {
        	excelFile = new XSSFWorkbook();
	    } catch (Exception e) {
			logger.info("data Load Failed");
			logger.fatal(e.toString());
			System.exit(1);
		}
        
    	final Sheet DEPOSIT_ACCOUNT_SHEET = excelFile.createSheet("일반 예금"); // 일반 예금 데이터를 저장할 시트 생성
    	final Sheet TIME_ACCOUNT_SHEET = excelFile.createSheet("정기 예금"); // 정기 예금 데이터를 저장할 시트 생성
    	final Sheet SAVING_ACCOUNT_SHEET  = excelFile.createSheet("정기 적금"); // 정기 적금 데이터를 저장할 시트 생성
    	final Sheet CHECK_ACCOUNT_SHEET  = excelFile.createSheet("자유 적금"); // 자유 적금 데이터를 저장할 시트 생성
        
    	// 일반 예금 부분
    	Row depositRow = DEPOSIT_ACCOUNT_SHEET.createRow(0); // 일반 예금 시트에 행 생성
    	
    	// 일반 예금 시트에 추가한 행에 표제목을 입력하는 반복문
    	for (int i = 0; i < ACCOUNT_EXCEL_LABELS.length; i++) {
			Cell labelCell = depositRow.createCell(i); // 셀 생성
			labelCell.setCellValue(ACCOUNT_EXCEL_LABELS[i]);
		}
    	
    	// 정기 예금,정기 적금,자유 적금 부분
        Row timeRow = TIME_ACCOUNT_SHEET.createRow(0); // 정기 예금 시트에 행 생성
        Row savingRow = SAVING_ACCOUNT_SHEET.createRow(0); // 정기 적금 시트에 행 생성
        Row checkRow = CHECK_ACCOUNT_SHEET.createRow(0); // 자유 적금 시트에 행 생성
            
    	// 정기 예금,정기 적금, 자유 적금 시트들에 추가한 행에 표제목을 입력하는 반복문
        // 입력 하는 표제목은 "이름","주민번호","계좌번호","비밀번호","잔액","마지막거래일자"
        for (int i = 0; i < ACCOUNT_SAVING_EXCEL_LABELS.length; i++) {
			Cell timeLabelCell = timeRow.createCell(i);
			Cell savingLabelCell = savingRow.createCell(i);
			Cell checkLabelCell = checkRow.createCell(i);
			
			timeLabelCell.setCellValue(ACCOUNT_SAVING_EXCEL_LABELS[i]);
			savingLabelCell.setCellValue(ACCOUNT_SAVING_EXCEL_LABELS[i]);
			checkLabelCell.setCellValue(ACCOUNT_SAVING_EXCEL_LABELS[i]);
		}
            
            
        if (!FILE_SYSTEM_PATH.exists()) { // File_System폴더가 있는지 확인
        	try{
        		FILE_SYSTEM_PATH.mkdir(); // File_System폴더가 없다면 File_System 폴더를 생성
    	        } 
    	        catch(Exception e){
    		    logger.error(e.toString());
    			System.exit(1);
    		}
			}
        
		if (!USER_DATA_PATH.exists()) { // File_System안에 user_date폴더가 있는지 확인
        	try{
        		USER_DATA_PATH.mkdir(); // File_System안에 user_date폴더가 없다면 user_date 폴더 생성
    	        }
	        catch(Exception e){
	        	// 다른 오류가 발생했을때 실행함
    	        logger.error(e.toString());
    			System.exit(1);
    		} 
  
		}
            
        readLimitTime(as);
        readLoanTime(as);
        try {
    	FileOutputStream fileOutputStream = new FileOutputStream(ACCOUNT_XLSX_FILE_PATH); // 엑셀파일생성
    	excelFile.write(fileOutputStream);
        fileOutputStream.close();
        excelFile.close();
        return;
        } catch (Exception e) {
        	// 다른 오류가 발생했을때 실행함
	    	logger.info("data Load Failed");
			logger.fatal(e.toString());
			System.exit(1);
        }
	}
	
	/*
	 * 메소드 : getLimitTime()
	 * 파라미터 : 없음(None)
	 * 반환값 : 없음(void) 
	 * 기능설명 : 	계좌에 제한사항(동결시간,출금한도,이체한도)를 user_limit.xlsx 엑셀 파일 1행 2열부터 가져와 BankMain에 있는 list에 저장된 객체만큼 맞춰 할당한다.
	 * 			0행은 표제목들이 1열은 계좌 번호들이 저장되어있어 가져오지않는다.
	 * 			만약 동결시간이 저장되어 있다면 BankMain에 있는 freezMap에 계좌번호를 키로 동결시간을 값으로 할당하지 않는다.
	 * 			동결시간이 저장되어있지 않다면 freezMap에 할당하지 않는다.
	 * 			만약 File_system 폴더안 user_date폴더에 user_limit.xlsx 엑셀 파일이 없다면 FileNotFoundException에러가 발생후 createLimitExcelFile을 실행한다
	 */
	
	// 저장된 제한들을 엑셀에서 읽어오는 메소드
	private void readLimitTime(AccountStorage accountStorage) {
		FileInputStream FileInputStream; // 엑셀(xlsx)파일의 경로를 담을 변수)
		int rowNumber = 1; // 행번호를 담을 변수(1행부터 시작합니다)
		LocalDateTime time = null; // 엑셀에 저장된 년월일시분초를 담을 변수
		long withdrawalLimit = 0; // 엑셀에 저장된 현재 출금액를 담을 변수(하루마다 초기화됨)
		long transferLimits = 0; // 엑셀에서 저장된 현재까지 이체금액를 담을 변수(하루마다 초기화됨)
		
		Workbook excelFile = null;
		
		try {
			// 엑셀파일은 프로젝트폴더의 File_system폴더안의 user_date폴더안의 user_limit.xlsx 엑셀파일입니다.
			FileInputStream = new FileInputStream(USER_LEMIT_XLSX_FILE_PATH);
			excelFile = WorkbookFactory.create(FileInputStream);
			
		} catch (FileNotFoundException e) {
			// 파일이 없을때 실행
			createLimitExcelFile();
			return;
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
			}
		
		Sheet sheet = excelFile.getSheetAt(0); // 시트는 0번 만 씁니다.

		for (Account list : accountStorage.getDepositAccountList()) {				
			Row row = sheet.getRow(rowNumber); // 행은 2번째 행부터 시작합니다.
			
			// 행이 비어있지않으면 실행합니다.
			if (row != null) {
				// 반복문 현재 행의 cell만큼 반복합니다.
				for (Cell cell : row) { {
					switch (cell.getColumnIndex()) {
						case 1:
							// LocalDateTime형식으로 엑셀에 저장된 시간을 불러옵니다.
							time = cell.getLocalDateTimeCellValue();
							
							
							list.setFreezTime(time);
							LocalDateTime now = LocalDateTime.now();
							
							// 현재 시간이 저장된 시간보다 이후면 실행합니다. 
							if (now.isAfter(time)) {
								list.setFreez(false);
								break;
							}
							
							//현재 시간이 저장된 시간보다 전이면 실행합니다. 
							list.setFreez(true);
							accountStorage.putFreezMap(list.getAccountNumber(), list.getFreezTime());
							break;
							
						case 2:
							//엑셀에 저장된 출금한도를 불러옵니다.
							withdrawalLimit = (long) cell.getNumericCellValue();
							list.setWithdrawalLimit(withdrawalLimit);
							break;
							
						case 3:
							//엑셀에 저장된 이체한도를 불러옵니다.
							transferLimits = (long) cell.getNumericCellValue();
							list.setTransferLimit(transferLimits);
							break;
					}
				}
				rowNumber++; // 행번호를 1증가시킵니다
			}
			} else {
				rowNumber++; // 행번호를 1증가시킵니다
				continue;
			}
				
		try {		
			excelFile.close();
			
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
			}
		}
	}
	
	// 대출 엑셀파일의 정보를 읽어오는 메소드
	private void readLoanTime(AccountStorage accountStorage) {
		FileInputStream FileInputStream; // 엑셀(xlsx)파일의 경로를 담을 변수)
		int rowNumber = 1; // 행번호를 담을 변수(2행부터 시작합니다)
		long loanLimit  = 0; // 엑셀에 저장된 현재 출금액를 담을 변수(하루마다 초기화됨)
		long principal  = 0; // 엑셀에서 저장된 현재까지 이체금액를 담을 변수(하루마다 초기화됨)
		int repaymentdate = 0;
		int repaymentperiod = 0;
		int interest = 0;
		double interestRate = 0;
		double interestPerMonth = 0.0;
		long principalPerMonth = 0;
		long principalAndInterest = 0;
		long principalAndInterestPerMonth = 0;
		
		Workbook excelFile = null;
		
		try {
			// 엑셀파일은 프로젝트폴더의 File_system폴더안의 user_date폴더안의 user_limit.xlsx 엑셀파일입니다.
			FileInputStream = new FileInputStream(USER_LOAN_XLSX_FILE_PATH);
			excelFile = WorkbookFactory.create(FileInputStream);
			
		} catch (FileNotFoundException e) {
			// 파일이 없을때 실행
			createloanExcelFile();
			return;
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
			}
		
		Sheet sheet = excelFile.getSheetAt(0); // 시트는 0번 만 씁니다.

		for (Account list : accountStorage.getDepositAccountList()) {				
			Row row = sheet.getRow(rowNumber); // 행은 2번째 행부터 시작합니다.
			
			// 행이 비어있지않으면 실행합니다.
			if (row != null) {
				// 반복문 현재 행의 cell만큼 반복합니다.
				for (Cell cell : row) { {
					switch (cell.getColumnIndex()) {
						case 1:
							//엑셀에 저장된 출금한도를 불러옵니다.
							loanLimit = (long) cell.getNumericCellValue();
							list.setLoanLimit(loanLimit);
							break;
							
						case 2:
							//원금
							principal = (long) cell.getNumericCellValue();
							list.setPrincipal(principal);
							break;
							
						case 3 :
							// 월 별 원금
							principalPerMonth = (long) cell.getNumericCellValue();
							list.setPrincipalPerMonth(principalPerMonth);
							break;
							
						case 4:
							//상환일
							repaymentdate = (int) cell.getNumericCellValue();
							list.setRepaymentdate(repaymentdate);
							break;
						case 5:
							// 상환기간(개월)
							repaymentperiod = (int)cell.getNumericCellValue();
							list.setRepaymentperiod(repaymentperiod);
							break;
							
						case 6:
							// 이자
							interest = (int)cell.getNumericCellValue();
							list.setInterest(interest);
							break;
							
						case 7:
							//이자율
							interestRate = cell.getNumericCellValue();
							list.setInterestRate(interestRate);
							break;
							
						case 8 :
							//월 별 이자
							interestPerMonth = cell.getNumericCellValue();
							list.setInterestPerMonth(interestPerMonth);
							break;
							
						case 9 :
							// 원리금
							principalAndInterest = (long) cell.getNumericCellValue();
							list.setPrincipalAndInterest(principalAndInterest);
							break;
							
						case 10 :
							//월 별 원리금
							principalAndInterestPerMonth = (long) cell.getNumericCellValue();
							list.setPrincipalAndInterestPerMonth(principalAndInterestPerMonth);
							break;
					}
				}
			}
				
				rowNumber++; // 행번호를 1증가시킵니다
			
			} else {
				rowNumber++; // 행번호를 1증가시킵니다
				continue;
			}
				
		try {		
			excelFile.close();
			
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
			}
		}
	}
	
	
	/*
	 * 메소드 : createLimitExcelFile()
	 * 파라미터 : 없음(None)
	 * 반환값 : 없음(void) 
	 * 기능설명 : 만약 File_system 폴더안 user_date폴더에 user_limit.xlsx 엑셀파일이 없을떼 File_system 폴더안 user_date폴더에 user_limit.xlsx 엑셀파일을 생성한다 
	 */
	
	private void createLimitExcelFile() {
		
        try {
        	Workbook excelFile = new XSSFWorkbook();
            Sheet sheet = excelFile.createSheet("계좌 제한 사항"); // 시트 생성
            Row row = sheet.createRow(0); // 행 생성
            
			for (int i = 0; i < 4; i++) {
				Cell cell = row.createCell(i); // 셀 생성
				switch (i) {
				case 0:
					cell.setCellValue("계좌번호");
					break;
				case 1:
					
					cell.setCellValue("동결 시간");
					break;
				case 2:
					cell.setCellValue("출금 금액");
					break;
				case 3:
					
					cell.setCellValue("이체 금액");
					break;
				}
			}
            
            
        	FileOutputStream fos = new FileOutputStream(USER_LEMIT_XLSX_FILE_PATH);
        	excelFile.write(fos);
        	excelFile.close();
            } catch (Exception e) {
        	logger.info("create Limit ExcelFile Failed");
			logger.fatal(e.toString());
			System.out.println("치명적인 오류가 발생하였습니다. 관리자에게 이야기해주십시오. 프로그램을 종료합니다.");
			System.exit(0);
        }
	}
	
	
	private void createloanExcelFile() {
		Workbook excelFile = null;
		Sheet sheet = null;
		Row row = null;
        try {
        	excelFile = new XSSFWorkbook();
            sheet = excelFile.createSheet("사용자 대출 상황"); // 시트 생성
            row = sheet.createRow(0); // 행 생성
            
        } catch (Exception e) {

        }
			for (int i = 0; i <= 9; i++) {
				Cell cell = row.createCell(i); // 셀 생성
				switch (i) {
				case 0:
					cell.setCellValue("계좌번호");
					break;
				case 1:
					cell.setCellValue("대출한도");
					break;
				case 2:
					cell.setCellValue("원금");
					break;
				case 3 :
					cell.setCellValue("월 별 원금");
					break;
				case 4:
					cell.setCellValue("상환일");
					break;
				case 5 :
					cell.setCellValue("상환 기간");
					break;
				case 6:
					cell.setCellValue("이자");
					break;
				case 7:
					cell.setCellValue("이자율");
					break;
				case 8 :
					cell.setCellValue("월 별 이자");
					break;
				case 9 :
					cell.setCellValue("원리금");
					break;
				case 10 :
					cell.setCellValue("월 별 원리금");
					break;
				}
			}
            
        try {   
        	FileOutputStream fos = new FileOutputStream(USER_LOAN_XLSX_FILE_PATH);
        	excelFile.write(fos);
        	excelFile.close();
            return;
        } catch (Exception e) {
        	logger.info("create Limit ExcelFile Failed");
			logger.fatal(e.toString());
			System.exit(1);
        }
  }
}
	
