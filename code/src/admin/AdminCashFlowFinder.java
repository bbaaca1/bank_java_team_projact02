package admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.regex.*;

public class AdminCashFlowFinder {
	
	private boolean isWithdrawal;
	
	// 기본 생성자 총출금액의 합계를 구합니다.
	public AdminCashFlowFinder(){}
	
	// 생성자 true가 들어왔을때 총입금의 합계를 구합니다.
	public AdminCashFlowFinder(boolean isWithdrawal){
		this.isWithdrawal = isWithdrawal;
	}
	
	
	// 이름을 입력시 이번달 이름의 총출금이나 총입금의 합계를 반환하는 메소드
    public long getMonthlyTotalTransactions(String name) {
    	isWithdrawal= false;
    	String fileName = null;
    	String withdrawalMoneyPattern = "withdrawal:\\s*(\\d+)"; // 로그에 저장된 withdrawal: 뒤에 있는 출금액을 찾기 위한 패턴
    	String depositDiaMoneyPattern = "deposit:\\s*(\\d+)"; // 로그에 저장된 deposit: 뒤에 있는 입금액을 찾기 위한 패턴
    	
    	if (!isWithdrawal) {
    		fileName = name +"-출금"; // 파일 이름은 (사용자명)-출금

		} else {
			fileName = name +"-입금"; // 파일 이름은 (사용자명)-출금
		}
    	String directoryPath = "data\\"+fileName; // 출금 로그파일 폴더 경로는 data\(사용자명)-출금 
    	// 출금 로그 파일 이름은(사용자명)-출금-(현재년월일).log
    	String month = String.format("%02d", LocalDate.now().getMonthValue());
//        String fileNamePattern = fileName+"-"+LocalDate.now().getYear()+"-"+month+ "-(3[01]|[12]\\d|0?[1-9]).log";
    	
    	String fileNamePattern = fileName+"-"+LocalDate.now().getYear()+"-"+month+ ".log"; // (사용자명)-출금(입금)-년-현재월.log 형식의 패턴을 지정
    	Pattern filePattern = Pattern.compile(fileNamePattern); // 현재 
    	
        long monthlyTotal = 0;
        
        File directory = new File(directoryPath); // 로그파일 폴더 경로는 data\(사용자명)-출금  

        if (directory.isDirectory()) {
            File[] files = directory.listFiles(); // 폴더안의 파일들을 담은 파일 배열
            
            if (files != null) {
            	//폴더가 비어있지않을때 실행
            	for (File file : files) {
            		//폴더안의 파일만큼 반복하는 반복문
            		
            		if (file.isFile()) {
            			//현재 지정된 파일이 파일일경우 실행
            			String currentFileName = file.getName(); // 현재 파일의 이름을 담을 변수 
						Matcher fileNameMatcher = filePattern.matcher(currentFileName);
								
            			if (fileNameMatcher.matches()) {
            				// 파일 경로는 루트디렉터리의 data\(사용자명)-출금\(사용자명)-출금-(현재년월일).log(상대경로)
            				String filePath = directoryPath+"\\"+currentFileName; 
                    	
            				try {
            					FileReader filereader = new FileReader(filePath);
            					BufferedReader bufReader = new BufferedReader(filereader);
            					String line ="";
        						Pattern regexPattern = null;
        						if (!isWithdrawal) {
            						regexPattern = Pattern.compile(withdrawalMoneyPattern);

								} else {
									regexPattern = Pattern.compile(depositDiaMoneyPattern);
								}
            					// 첫줄부터 마지막 줄까지 한줄씩 읽어 오는 반복문(만약 해당줄이 비어있다면 종료)
            					while((line = bufReader.readLine()) != null) {

            						Matcher moneyMatcher = regexPattern.matcher(line);
				                
            						if (moneyMatcher.find()) {
            							monthlyTotal += Integer.parseInt(moneyMatcher.group(1));
									
            						}

            					}
            					bufReader.close(); //버퍼로더 닫기
            				} catch (Exception e) {
            					break;
            				}

            			}
            		}
            	}
            }
            
        }
		return monthlyTotal;

    }
    
    // 이름과 숫자가 입력됬을때 입력한 숫자만큼 감소된 월의 입금 또는 출금량의 총합을 찾아반환하는 메소드
    public long getMonthlyTotalTransactions(String name, int previousMonth) {
    	String fileName = null;
    	String withdrawalMoneyPattern = "withdrawal:\\s*(\\d+)"; // 로그에 저장된 withdrawal: 뒤에 있는 출금액을 찾기 위한 패턴
    	String depositDiaMoneyPattern = "deposit:\\s*(\\d+)"; // 로그에 저장된 deposit: 뒤에 있는 입금액을 찾기 위한 패턴
    	
    	isWithdrawal = true;
    	
    	if (!isWithdrawal) {
    		fileName = name +"-출금"; // 파일 이름은 (사용자명)-출금

		} else {
			fileName = name +"-입금"; // 파일 이름은 (사용자명)-출금
		}
    	
    	String directoryPath = "data\\"+fileName; // 출금 로그파일 폴더 경로는 data\(사용자명)-출금 
    	// 출금 로그 파일 이름은(사용자명)-출금-(현재년월일).log
    	String month = String.format("%02d", LocalDate.now().minusMonths(previousMonth).getMonthValue());
//        String fileNamePattern = fileName+"-"+LocalDate.now().getYear()+"-"+month+ "-(3[01]|[12]\\d|0?[1-9]).log"; 
    	String fileNamePattern = fileName+"-"+LocalDate.now().getYear()+"-"+month+ ".log";
        
        long monthlyTotal = 0;
        
        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
        	
            File[] files = directory.listFiles(); // 폴더안의 파일들을 담은 파일 배열
            
            if (files != null) {
            	for (File file : files) {
            		if (file.isFile()) {
            			
            			String currentFileName = file.getName();
            			Pattern filePattern = Pattern.compile(fileNamePattern);
						Matcher fileNameMatcher = filePattern.matcher(currentFileName);
								
            			if (fileNameMatcher.matches()) {
            				// 파일 경로는 루트디렉터리의 data\(사용자명)-출금\(사용자명)-출금-(현재년월일).log(상대경로)
            				String filePath = directoryPath+"\\"+currentFileName; 
                    	
            				try {
            					FileReader filereader = new FileReader(filePath);
            					BufferedReader bufReader = new BufferedReader(filereader);
            					
            					String line =""; //로그 파일의 텍스트를 한줄씩 저장할 변수
        						Pattern regexPattern = null; //패턴을 담아둘 변수
        						
        						//만약 현재 출금일때
        						if (!isWithdrawal) {
            						regexPattern = Pattern.compile(withdrawalMoneyPattern);

								} else {
									regexPattern = Pattern.compile(depositDiaMoneyPattern);
								}
        						
            					// 첫줄부터 마지막 줄까지 한줄씩 읽어 오는 반복문(만약 해당줄이 비어있다면 종료)
            					while((line = bufReader.readLine()) != null) {

            						Matcher moneyMatcher = regexPattern.matcher(line);
				                
            						if (moneyMatcher.find()) {
            							monthlyTotal += Integer.parseInt(moneyMatcher.group(1));
									
            						}

            					}            					bufReader.close(); //버퍼로더 닫기
            				} catch (Exception e) {
            					break;
            				}

            			}
            		}
            	}
            }
            
        }
		return monthlyTotal;

    }
    
}

