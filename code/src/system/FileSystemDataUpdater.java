package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileSystemDataUpdater {
	// 로깅용 로그 선언
	private final static Logger logger = LogManager.getLogger(FileSystemDataUpdater.class);
	
	//
	private int selectedRowNumber = 0;
	private Row selectedRow; 
	//
	private final Account selectAccount;
	private final int seletedAccountIndex;
	private final String seletedAccountType;
	
	//
	private static final String ACCOUNT_XLSX_FILE_PATH = "File_system\\user_data\\accounts.xlsx"; // 계좌 정보가 들어있는 엑셀파일의 위치의 상대경로
	private static final String USER_LEMIT_XLSX_FILE_PATH = "File_system\\user_data\\user_limit.xlsx"; // 일반 계좌의 제한정보를 가지고 있는 엑셀 파일의 상대경로
	private static final String USER_LOAN_XLSX_FILE_PATH = "File_system\\user_data\\user_loan.xlsx"; // 일반 계좌의 대출정보를 가지고 있는 엑셀 파일의 상대경로
	
	
//	// 기본 생성자 생성한 예금 계좌의 정보를 엑셀에 저장합니다.
//	public FileSystemDataUpdater(Account selectAccount) {
//		seletedAccountType = selectAccount.getAccountType();
//		this.selectAccount = selectAccount;
//	}
	
	// 기본 생성자 지정한 Account 객체에 수정된 정보를 엑셀에 저장합니다.
	public FileSystemDataUpdater(Account selectAccount,int seletedAccountIndex) {
		this.selectAccount = selectAccount;
		this.seletedAccountIndex = seletedAccountIndex;
		this.seletedAccountType = selectAccount.getAccountType();
	}
	
	
	public void updateExcelDate() {
		// list의 정보를 엑셀파일에 입력하는 메소드입니다.
		
		if (seletedAccountType.equals("일반 예금")) {
			writeDepositAccountDateToExcel();
			return;
		} else {
			writeOtherAccountDateToExcel();
			return;
		}
		
	}
	
	
	// 새로생성한 예금 계좌를 추가하는 메소드 
	private void writeDepositAccountDateToExcel() {
		
		Workbook workbook = null;
		
		try {
			final FileInputStream fileInputStream = new FileInputStream(ACCOUNT_XLSX_FILE_PATH);
			workbook = WorkbookFactory.create(fileInputStream);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
		
		Sheet depositAccountSheet = workbook.getSheetAt(0);
		
		//
		do {
			selectedRowNumber++; // 0행은 제외할것이므로 먼저 증가시킨다
			selectedRow = depositAccountSheet.getRow(selectedRowNumber);
			
		} while (selectedRow != null);
		
		//
		selectedRow = depositAccountSheet.createRow(selectedRowNumber);
		Cell newCell = null;
		String str = null; //문자의 형식(format)을 변경하여 엑셀에 쓰기위한 변수
		Date time = null; //시간을 Date타입으로 변환시켜 엑셀에 쓰기위한 변수
		
		for (int i = 0; i < 12; i++) {
			newCell = selectedRow.createCell(i);
			
			switch (i) {
			case 0:
				// 이름 입력 (
				newCell.setCellValue(selectAccount.getName());
				break;
			case 1:
				// 주민 번호 입력
				str = String.format("%s-%s",
						selectAccount.getSsn().substring(0,6),
						selectAccount.getSsn().substring(6));
				newCell.setCellValue(str); // 엑셀에 주민 번호를 6자리-7자리 형식으로 저장
				break;
				
			case 2:
				// 계좌 번호 입력
				str = String.format("%s-%s-%s",
						selectAccount.getAccountNumber().substring(0,3),
						selectAccount.getAccountNumber().substring(3,6),
						selectAccount.getAccountNumber().substring(6));
				newCell.setCellValue(str); // 엑셀에 계좌 번호를 012-068-(일련번호) 형식으로 저장
			break;
			
			case 3:
				// 비밀번호
				newCell.setCellValue(selectAccount.getAccountPasswd());
				break;
				
			case 4:
				// 잔액 입력
				newCell.setCellValue(selectAccount.getBalance());
				break;
			case 5:
				// 마지막 거래 일자
				time = Date.from(selectAccount.getLastTradingDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 가져옴
				break;
				
			case 6:
				// 주소 입력
				newCell.setCellValue(selectAccount.getHomeAddress());
				break;
				
			case 7:
				// 전화 번호 입력
				str = String.format("%s-%s-%s",
						selectAccount.getPhoneNumber().substring(0,3),
						selectAccount.getPhoneNumber().substring(3,7),
						selectAccount.getPhoneNumber().substring(7));
				newCell.setCellValue(str); // 엑셀에 전화번호를 010-1234-1234형식으로 저장
				break;

			case 8:
				// 계좌 상태
				newCell.setCellValue(selectAccount.getState());
				break;
				
			case 9:
				// 신용점수
				newCell.setCellValue(selectAccount.getCreditScore());
				break;
				
			case 10:
				// 우대고객 여부
				newCell.setCellValue(selectAccount.getVip());
				break;
			}
		}
			
		// 계좌 설정 완료
		// 멀티맵에 키와 값 추가 키1: 현재 계좌번호 키2: 현재 계좌비밀번호 값: 현재 계좌객체가 저장된 어레이 리스트의 인덱스번호
//		accountStorage.putMultikeyMap(account.getAccountNumber(), account.getAccountPasswd(), index); 
//		accountStorage.putMap(account.getAccountNumber(), index);// 맵에 키와 값 추가 키: 현재 계좌번호 값: 현재 계좌객체가 저장된 어레이 리스트의 인덱스번호
		
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(new File(ACCOUNT_XLSX_FILE_PATH));
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
		} catch (Exception e) {
			logger.error(e.toString());
			
		}
		
	}
	
	// 예금 계좌를 제외한 다른 계좌(정기예금,정기적금,자유적금)의 정보를 엑셀에 저장하는 메소드
	private void writeOtherAccountDateToExcel() {
		
		Workbook workbook = null;
		Sheet accountSheet = null;
		try {
			final FileInputStream fileInputStream = new FileInputStream(ACCOUNT_XLSX_FILE_PATH);
			workbook = WorkbookFactory.create(fileInputStream);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
		
		accountSheet = workbook.getSheet(seletedAccountType); //시트명과 계좌의 종류 명과 같으므로 계좌 종류 명으로 시트를 찾음
		
		//엑셀의 빈행을 찾을때까지 돌리는 반복문
		do {
			selectedRowNumber++; // 0행은 제외할것이므로 먼저 증가시킨다
			selectedRow = accountSheet.getRow(selectedRowNumber);
		} while (selectedRow != null);
		
		selectedRow = accountSheet.createRow(selectedRowNumber);
		Cell newCell = null;		
		String str = null; //문자의 형식(format)을 변경하여 엑셀에 쓰기위한 변수
		Date time = null; //시간을 Date타입으로 변환시켜 엑셀에 쓰기위한 변수
		
		// 엑셀의 저장시키는 부분
		for (int i = 0; i < 8; i++) {
			newCell = selectedRow.createCell(i);
			
			switch (i) {
			case 0:
				// 이름 입력 (
				newCell.setCellValue(selectAccount.getName());
				break;
			case 1:
				// 주민 번호 입력
				str = String.format("%s-%s",
						selectAccount.getSsn().substring(0,6),
						selectAccount.getSsn().substring(6));
				newCell.setCellValue(str); // 엑셀에 주민 번호를 6자리-7자리 형식으로 저장
				break;
				
			case 2:
				// 계좌 번호 입력
				str = String.format("%s-%s-%s",
						selectAccount.getAccountNumber().substring(0,3),
						selectAccount.getAccountNumber().substring(3,6),
						selectAccount.getAccountNumber().substring(6));
				newCell.setCellValue(str); // 엑셀에 계좌 번호를 012-068-(일련번호) 형식으로 저장
			break;
			
			case 3:
				// 비밀번호
				newCell.setCellValue(selectAccount.getAccountPasswd());
				break;
				
			case 4:
				// 잔액 입력
				newCell.setCellValue(selectAccount.getBalance());
				break;
				
			case 5:
				// 납기 일자
				time = Date.from(selectAccount.getAccountDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
				break;
				
			case 6:
				// 만기 일자
				time = Date.from(selectAccount.getAccountMaturity().atStartOfDay(ZoneId.systemDefault()).toInstant());
				newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
				break;
				
			case 7:
				// 마지막 거래 일자
				time = Date.from(selectAccount.getLastTradingDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
				break;
				
			}
		}
		
		// 계좌 설정 완료
		// 멀티맵에 키와 값 추가 키1: 현재 계좌번호 키2: 현재 계좌비밀번호 값: 현재 계좌객체가 저장된 어레이 리스트의 인덱스번호
//		accountStorage.putMultikeyMap(account.getAccountNumber(), account.getAccountPasswd(), index); 
//		accountStorage.putMap(account.getAccountNumber(), index);// 맵에 키와 값 추가 키: 현재 계좌번호 값: 현재 계좌객체가 저장된 어레이 리스트의 인덱스번호
		
		FileOutputStream outFile;
		try {
			outFile = new FileOutputStream(new File(ACCOUNT_XLSX_FILE_PATH));
			workbook.write(outFile);
			outFile.close();
			workbook.close();
		} catch (Exception e) {
			logger.error(e.toString());
			
		}
		
		}

	// 수정 사항을 엑셀에 입력하는 메소드
	public void ModifyAccountDateToExcel() {	
		Workbook workbook = null;
		String str = null;
		Date time = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(ACCOUNT_XLSX_FILE_PATH);
			workbook = WorkbookFactory.create(fileInputStream);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
		
		Sheet accountSheet = workbook.getSheet(selectAccount.getAccountType());
		selectedRow = accountSheet.getRow(seletedAccountIndex+1);
		
		if(selectAccount.getAccountType().equals("일반 예금")) {
			// 일반 예금 일때 입력하는 정보들
			for (Cell newCell : selectedRow) {
				
				switch (newCell.getColumnIndex()) {
				case 0:
					// 이름 입력 (
					newCell.setCellValue(selectAccount.getName());
					break;
				case 1:
					// 주민 번호 입력
					str = String.format("%s-%s",
							selectAccount.getSsn().substring(0,6),
							selectAccount.getSsn().substring(6));
					newCell.setCellValue(str); // 엑셀에 주민 번호를 6자리-7자리 형식으로 저장
					break;
					
				case 2:
					// 계좌 번호 입력
					str = String.format("%s-%s-%s",
							selectAccount.getAccountNumber().substring(0,3),
							selectAccount.getAccountNumber().substring(3,6),
							selectAccount.getAccountNumber().substring(6));
					newCell.setCellValue(str); // 엑셀에 계좌 번호를 012-068-(일련번호) 형식으로 저장
				break;
				
				case 3:
					// 비밀번호
					newCell.setCellValue(selectAccount.getAccountPasswd());
					break;
					
				case 4:
					// 잔액 입력
					newCell.setCellValue(selectAccount.getBalance());
					break;
				case 5:
					// 마지막 거래 일자
					time = Date.from(selectAccount.getLastTradingDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
					newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 가져옴
					break;
					
				case 6:
					// 주소 입력
					newCell.setCellValue(selectAccount.getHomeAddress());
					break;
					
				case 7:
					// 전화 번호 입력
					str = String.format("%s-%s-%s",
							selectAccount.getPhoneNumber().substring(0,3),
							selectAccount.getPhoneNumber().substring(3,7),
							selectAccount.getPhoneNumber().substring(7));
					newCell.setCellValue(str); // 엑셀에 전화번호를 010-1234-1234형식으로 저장
					break;
	
				case 8:
					// 계좌 상태
					newCell.setCellValue(selectAccount.getState());
					break;
					
				case 9:
					// 신용점수
					newCell.setCellValue(selectAccount.getCreditScore());
					break;
					
				case 10:
					// 우대고객 여부
					newCell.setCellValue(selectAccount.getVip());
					break;
				}
			} 
		}else {
			// 일반 예금 외 계좌일때 엑셀에 입력하는 정보들 
			for (Cell newCell : selectedRow) {
				
				switch (newCell.getColumnIndex()) {
				case 0:
					// 이름 입력 (
					newCell.setCellValue(selectAccount.getName());
					break;
				case 1:
					// 주민 번호 입력
					str = String.format("%s-%s",
							selectAccount.getSsn().substring(0,6),
							selectAccount.getSsn().substring(6));
					newCell.setCellValue(str); // 엑셀에 주민 번호를 6자리-7자리 형식으로 저장
					break;
					
				case 2:
					// 계좌 번호 입력
					str = String.format("%s-%s-%s",
							selectAccount.getAccountNumber().substring(0,3),
							selectAccount.getAccountNumber().substring(3,6),
							selectAccount.getAccountNumber().substring(6));
					newCell.setCellValue(str); // 엑셀에 계좌 번호를 012-068-(일련번호) 형식으로 저장
				break;
				
				case 3:
					// 비밀번호
					newCell.setCellValue(selectAccount.getAccountPasswd());
					break;
					
				case 4:
					// 잔액 입력
					newCell.setCellValue(selectAccount.getBalance());
					break;
					
				case 5:
					// 납기일
					time = Date.from(selectAccount.getAccountDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
					newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
					break;
					
				case 6:
					// 만기일
					time = Date.from(selectAccount.getAccountMaturity().atStartOfDay(ZoneId.systemDefault()).toInstant());
					newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
					break;
					
				case 7:
					// 마지막 거래 일자
					time = Date.from(selectAccount.getLastTradingDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
					newCell.setCellValue(time); // 엑셀에 마지막 거래일자를 Date 형식으로 입력함
					break;
				}	
			}
		}
		
        try (FileOutputStream fos = new FileOutputStream(ACCOUNT_XLSX_FILE_PATH)) {
            workbook.write(fos);
        } catch (Exception e) {
        	logger.error(e.toString());
        }
        
	}
	
	
	public void removeAccount() {	
		Workbook accontWorkbook = null;
		Workbook limitWorkbook = null;
		Workbook loanWorkbook = null;
		boolean isRowEmpty = false;
		try {
			FileInputStream fileInputStreamAccount = new FileInputStream(ACCOUNT_XLSX_FILE_PATH);
			FileInputStream fileInputStreamLimit = new FileInputStream(USER_LEMIT_XLSX_FILE_PATH);
			FileInputStream fileInputStreamLoan = new FileInputStream(USER_LOAN_XLSX_FILE_PATH);
			
			accontWorkbook = WorkbookFactory.create(fileInputStreamAccount);
			limitWorkbook = WorkbookFactory.create(fileInputStreamLimit);
			loanWorkbook = WorkbookFactory.create(fileInputStreamLoan);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
		
		Sheet accountSheet = accontWorkbook.getSheet(selectAccount.getAccountType());
		Sheet limitSheet = limitWorkbook.getSheetAt(0);
		Sheet loanSheet = loanWorkbook.getSheetAt(0);
		
        Row rowToRemoveAccount = accountSheet.getRow(seletedAccountIndex+1);
        
        accountSheet.removeRow(rowToRemoveAccount);
        // 계좌 엑셀 빈행 제거 반복문
        for(int i = 0; i < accountSheet.getLastRowNum(); i++){
            if(accountSheet.getRow(i)==null){
                isRowEmpty=true;
                accountSheet.shiftRows(i + 1, accountSheet.getLastRowNum(), -1);
                i--;
            continue;
            }
            for(int j =0; j<accountSheet.getRow(i).getLastCellNum();j++){
                if(accountSheet.getRow(i).getCell(j).toString().trim().equals("")){
                    isRowEmpty=true;
                }else {
                    isRowEmpty=false;
                    break;
                }
            }
            if(isRowEmpty==true){
            	accountSheet.shiftRows(i + 1, accountSheet.getLastRowNum(), -1);
                i--;
            }
        }
        
        // 삭제하는 계좌가 일반 예금일때 실행 
        if (selectAccount.getAccountType().equals("일반 예금")) {
        	if (!(limitSheet.getLastRowNum() >= seletedAccountIndex+1) && !(loanSheet.getLastRowNum() >= seletedAccountIndex+1)) {
				
			}
        	Row rowToRemoveLimit = limitSheet.getRow(seletedAccountIndex+1);
        	Row rowToRemoveLoan = loanSheet.getRow(seletedAccountIndex+1);
        	
        	limitSheet.removeRow(rowToRemoveLimit);
        	loanSheet.removeRow(rowToRemoveLoan);
        	
        	// 제한 정보 엑셀의 빈행을 지우는 반복문
            for(int i = 0; i < limitSheet.getLastRowNum(); i++){
                if(limitSheet.getRow(i)==null){
                    isRowEmpty=true;
                    limitSheet.shiftRows(i + 1, limitSheet.getLastRowNum(), -1);
                    i--;
                continue;
                }
                for(int j =0; j<limitSheet.getRow(i).getLastCellNum();j++){
                    if(accountSheet.getRow(i).getCell(j).toString().trim().equals("")){
                        isRowEmpty=true;
                    }else {
                        isRowEmpty=false;
                        break;
                    }
                }
                if(isRowEmpty==true){
                	limitSheet.shiftRows(i + 1, limitSheet.getLastRowNum(), -1);
                    i--;
                }
            }
            
            // 대출 정보 엑셀의 빈행을 지우는 반복문
            for(int i = 0; i < loanSheet.getLastRowNum(); i++){
                if(loanSheet.getRow(i)==null){
                    isRowEmpty=true;
                    loanSheet.shiftRows(i + 1, loanSheet.getLastRowNum(), -1);
                    i--;
                continue;
                }
                for(int j =0; j<loanSheet.getRow(i).getLastCellNum();j++){
                    if(loanSheet.getRow(i).getCell(j).toString().trim().equals("")){
                        isRowEmpty=true;
                    }else {
                        isRowEmpty=false;
                        break;
                    }
                }
                if(isRowEmpty==true){
                	loanSheet.shiftRows(i + 1, loanSheet.getLastRowNum(), -1);
                    i--;
                }
            }
		}
          
        try {
        	FileOutputStream fileOutputStreamAccount = new FileOutputStream(ACCOUNT_XLSX_FILE_PATH);
        	FileOutputStream fileOutputStreamlimit = new FileOutputStream(USER_LEMIT_XLSX_FILE_PATH);
        	FileOutputStream fileOutputStreamloan = new FileOutputStream(USER_LOAN_XLSX_FILE_PATH);
        	accontWorkbook.write(fileOutputStreamAccount);
        	
			if (selectAccount.getAccountType().equals("일반 예금")) {
	        	limitWorkbook.write(fileOutputStreamlimit);
				loanWorkbook.write(fileOutputStreamloan);
			}
			
			accontWorkbook.close();
			limitWorkbook.close();
			loanWorkbook.close();
			
        } catch (Exception e) {
        	logger.error(e.toString());
        }
	}
	
	
	
	/*
	 * 메소드 : writeLimitsToExcel()
	 * 파라미터 : LocalDateTime freezTime
	 * 반환값 : 없음(void) 
	 * 기능설명 : 	엑셀의 시트를 삭제한후 첫번째행(0)에 계좌번호, 동결 시간, 출금 금액, 이체 금액이라는 표제목을 입력한다
	 * 			계좌에 제한사항(동결시간,출금 금액,이체 금액)과 계좌번호를 를 BankMain에 있는 list로 부터 불러와 엑셀파일에 1행부터 계좌번호 동결시간 출금금액 이체금액을 저장한다
	 * 			만약 동결시간이 저장되어 있다면 동결시간은 0으로 저장된다
	 */
	
	// 제한 시간을 엑셀에 입력하는 메소드
	public void writeLimitsToExcel() {
		Workbook workbook = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(USER_LEMIT_XLSX_FILE_PATH);
			workbook = WorkbookFactory.create(fileInputStream);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
				
		Sheet accountSheet = workbook.getSheetAt(0);
		selectedRow = accountSheet.getRow(seletedAccountIndex+1);
		
		// 엑셀에 계좌번호,동결시간,현재출금금액,현재이채금액을 입력부분 
		if (selectedRow != null) {

            Cell cell = selectedRow.getCell(0);
            
            String accountNumber = String.format("%s-%s-%s",
					selectAccount.getAccountNumber().substring(0,3),
					selectAccount.getAccountNumber().substring(3,6),
					selectAccount.getAccountNumber().substring(6));
            cell.setCellValue(accountNumber); // 엑셀에 계좌번호를 012-068-******형식으로 저장합니다.
            
            cell = selectedRow.createCell(1);
            
            // 동결계좌가 아니면 동결시간을 1900년 01월 01일 0시 0분으로 설정합니다.
            if (!selectAccount.isFreez()) {
            	cell.setCellValue(LocalDateTime.of(1900, 1, 1, 0, 0));
            } else {
            	cell.setCellValue(selectAccount.getFreezTime());
            }

            cell = selectedRow.getCell(2);
            cell.setCellValue(selectAccount.getWithdrawalLimit());
            
            cell = selectedRow.getCell(3);
            cell.setCellValue(selectAccount.getTransferLimit());
	            
		} else {
			// 만약 신규 계좌라면 실행합니다.
			selectedRow = accountSheet.createRow(seletedAccountIndex+1);
			
            Cell cell = selectedRow.createCell(0);
            
            String accountNumber = String.format("%s-%s-%s",
					selectAccount.getAccountNumber().substring(0,3),
					selectAccount.getAccountNumber().substring(3,6),
					selectAccount.getAccountNumber().substring(6));
            cell.setCellValue(accountNumber); // 엑셀에 계좌번호를 012-068-******형식으로 저장합니다. 
            
            cell = selectedRow.createCell(1);
            
            if (!selectAccount.isFreez()) {
            	LocalDateTime localDateTime = LocalDateTime.of(1900, 1, 1, 0, 0);
            	cell.setCellValue(localDateTime);
            } else {
            	cell.setCellValue(selectAccount.getFreezTime());
            }

            cell = selectedRow.createCell(2);
            cell.setCellValue(selectAccount.getWithdrawalLimit());

            cell = selectedRow.createCell(3);
            cell.setCellValue(selectAccount.getTransferLimit());
		}
            
		try {	
			FileOutputStream fileOutputStream = new FileOutputStream(new File(USER_LEMIT_XLSX_FILE_PATH));
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
			
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
		}

	}
	
	// 계좌 대출정보를 엑셀에 입력하는 메소드
	public void writeLoanToExcel() {
		Workbook workbook = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(USER_LOAN_XLSX_FILE_PATH);
			workbook = WorkbookFactory.create(fileInputStream);
		} catch (IOException e) {
		    logger.error("An IOException occurred while reading the file.", e);
		}
		
		Sheet accountSheet = workbook.getSheetAt(0);
		
		selectedRow = accountSheet.getRow(seletedAccountIndex+1);
		
		// 엑셀에 대출관련 정보들을 입력하는 메소드 
		if (selectedRow != null) {
			for (int i = 0; i < 10; i++) {
				Cell cell = selectedRow.getCell(i);
				
				switch (i) {
				case 0:
					// 계좌 정보
		            String accountNumber = String.format("%s-%s-%s",
							selectAccount.getAccountNumber().substring(0,3),
							selectAccount.getAccountNumber().substring(3,6),
							selectAccount.getAccountNumber().substring(6));
		            cell.setCellValue(accountNumber); // 엑셀에 계좌번호를 012-068-******형식으로 저장합니다.
					break;
					
				case 1:
					//대출 한도
					cell.setCellValue(selectAccount.getLoanLimit());
					break;
					
				case 2:
					// 원금
		            cell.setCellValue(selectAccount.getPrincipal());
					break;
					
				case 3:
					// 월 별 원금
					cell.setCellValue(selectAccount.getPrincipalPerMonth());
					break;
				case 4:
					// 상환일
					cell.setCellValue(selectAccount.getRepaymentdate());
					break;
				case 5:
					// 상환기간
					cell.setCellValue(selectAccount.getRepaymentperiod());
					break;
				case 6:
					// 이자
					cell.setCellValue(selectAccount.getInterest());
					break;
				case 7:
					// 이자율
					cell.setCellValue(selectAccount.getInterestRate());
					break;
				case 8:
					// 월 별 이자
					cell.setCellValue(selectAccount.getInterestPerMonth());
					break;
				case 9:
					// 원리금
					cell.setCellValue(selectAccount.getPrincipalAndInterest());
					break;
				case 10:
					// 월 별 원리금
					cell.setCellValue(selectAccount.getPrincipalAndInterestPerMonth());
					break;

				}
			}
	            
		} else {
			// 만약 신규 계좌라면 실행합니다.
			selectedRow = accountSheet.createRow(seletedAccountIndex+1);
			for (int i = 0; i < 10; i++) {
				Cell cell = selectedRow.createCell(i);
				switch (i) {
				case 0:
					// 계좌 번호
		            String accountNumber = String.format("%s-%s-%s",
							selectAccount.getAccountNumber().substring(0,3),
							selectAccount.getAccountNumber().substring(3,6),
							selectAccount.getAccountNumber().substring(6));
		            cell.setCellValue(accountNumber); // 엑셀에 계좌번호를 012-068-******형식으로 저장합니다.
					break;
					
				case 1:
					//대출 한도
					cell.setCellValue(selectAccount.getLoanLimit());
					break;
					
				case 2:
					// 원금
		            cell.setCellValue(selectAccount.getPrincipal());
					break;
					
				case 3:
					// 월 별 원금
					cell.setCellValue(selectAccount.getPrincipalPerMonth());
					break;
					
				case 4:
					// 상환일
					cell.setCellValue(selectAccount.getRepaymentdate()); // 엑셀에 상환일 Date 형식으로 입력
					break;
				case 5:
					// 상환기간
					cell.setCellValue(selectAccount.getRepaymentperiod());
					break;
					
				case 6:
					// 이자
					cell.setCellValue(selectAccount.getInterest());
					break;
					
				case 7:
					// 이자율
					cell.setCellValue(selectAccount.getInterestRate());
					break;
					
				case 8:
					// 월 별 이자
					cell.setCellValue(selectAccount.getInterestPerMonth());
					break;
					
				case 9:
					// 원리금
					cell.setCellValue(selectAccount.getPrincipalAndInterest());
					break;
					
				case 10:
					// 월 별 원리금
					cell.setCellValue(selectAccount.getPrincipalAndInterestPerMonth());
					break;

				}
			}
		}
            
		try {	
			FileOutputStream fileOutputStream = new FileOutputStream(new File(USER_LOAN_XLSX_FILE_PATH));
			workbook.write(fileOutputStream);
			fileOutputStream.close();
			workbook.close();
			
		} catch (Exception e) {
			// 에러 발생시 프로그램을 종료시킴
			logger.fatal(e.toString());
			System.exit(0);
		}

	}
	
	
	
}
