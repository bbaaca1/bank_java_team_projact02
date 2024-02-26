package system;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Account {
	// 계좌 생성 및 사용용 클래스입니다.
	//필드(계좌용 기본 변수들)
	private String name; // 예금주명
	private String accountNumber; // 계좌번호
	private String ssn; // 주민등록번호
	private String homeAddress; // 집주소
	private String phoneNumber;  // 전화 번호
	private long balance; // 잔액
	private String accountType;  // 계좌 유형(현재 일반 계좌, 정기 적금)
	private LocalDate lastTradingDate; // 최종 거래 일자
	private String accountPasswd; // 거래 비밀 번호
	private int interest; //이자

	
	//필드(계좌용 추가 변수들)
	private int state; // 계좌 상태
	private int creditScore; // 신용점수
	private String vip; // 사용자의 우대고객 여부
	private long withdrawalLimit = 0; // 출금 한도 설정 변수
	private long transferLimit = 0; // 이체 한도 설정 변수
	private LocalDateTime freezTime; // 동결 시간 설정 변수 
	private long loanLimit; // 대출 한도
	private long principal; // 원금
	private int repaymentdate; // 상환일
	private int repaymentperiod; // 상환기간
	private double interestRate; // 이자율
	private double interestPerMonth; // 월 별 이자
	private long principalPerMonth; // 월 별 원금
	private long principalAndInterest; // 원리금
	private long principalAndInterestPerMonth; // 월별 원리금
	
	private double depositInterestRate; // 일반 예금 이자율
	private double timeInterestRate; // 정기 예금 이자율
	private double savingInterestRate; // 정기 적금 이자율
	private double freeInterestRate; // 자유 적금 이자율
	
	//정기 적금,예금,자유 적금 용 변수
    private LocalDate accountMaturity; // 만기일
    private LocalDate accountDueDate; // 납기일
	
	
	
	// 필드 계좌 상태용 변수들
	private boolean isFreezing = false; // 동결상태 확인 변수
	private boolean isNonTradingAccing = false; // 휴면 계좌 확인 변수
	private boolean isWithLimiting = false; // 출금 제한 확인 변수
	private boolean isTransferLimiting = false; // 이체 제한 확인 변수
	

	// 생성자
	// 엑셀에 저장된 일반예금의 정보를 담은 객체를 저장하기 위한 생성자
	public Account(String name, String accountNumber, String ssn, String homeAddress, String phoneNumber,
			long balance, String accountType, LocalDate date,String accountPasswd, int state,
			int creditScore, String vip) {
		
		this.name = name;
		this.accountNumber = accountNumber;
		this.ssn = ssn;
		this.homeAddress = homeAddress;
		this.phoneNumber = phoneNumber;
		this.balance = balance;
		this.accountType = accountType;
		this.lastTradingDate = date;
		this.accountPasswd = accountPasswd;
		this.state = state;
		this.creditScore = creditScore;
		this.vip = vip;
		
	}
	
	// 엑셀에 저장된 정기예금,정기적금,자유적금의 정보를 담은 객체를 저장하기 위한 생성자
	public Account(String name, String accountNumber, String ssn,String accountPasswd,long balance,
				   String accountType,LocalDate lastTradingDate,
				   LocalDate accountMaturity,LocalDate accountDueDate) {
		this.name = name;
		this.accountNumber = accountNumber;
		this.ssn = ssn;
		this.balance = balance;
		this.accountType = accountType;
		this.lastTradingDate = lastTradingDate;
		this.accountPasswd = accountPasswd;
		this.accountMaturity = accountMaturity;
		this.accountDueDate = accountDueDate;
	}
	
	// 일반 예금 계좌 생성시 입력값을 받아 생성하기 위한 생성자
	public Account(String name, String accountNumber, String ssn, String homeAddress, String phoneNumber, String accountPasswd) {
		this.name = name;
		this.accountNumber = accountNumber;
		this.ssn = ssn;
		this.homeAddress = homeAddress;
		this.phoneNumber = phoneNumber;
		this.balance = 1;
		this.accountType = "일반 예금";
		this.accountPasswd = accountPasswd;
		this.state = 0;
		this.lastTradingDate = LocalDate.now();
		this.creditScore = 50;
		this.vip = "일반 고객";
		this.loanLimit = 0;
		this.principal  = 0;
		this.repaymentdate  = 0;
		this.repaymentperiod  = 0;
		this.interestRate = 0.0;
		this.interestPerMonth = 0.0;
		this.principalPerMonth  = 0;
		this.principalAndInterest = 0;
		this.principalAndInterestPerMonth = 0; 
		this.depositInterestRate = 0.1;
		
	}
	
	// 정기 예금,정기 적금,자유 적금 계좌 생성시 입력값을 받아 생성하기 위한 생성자
	public Account(String name, String accountNumber, String ssn,
			String accountType, String accountPasswd,LocalDate accountMaturity
			,LocalDate accountDueDate) {
		this.name = name;
		this.accountNumber = accountNumber;
		this.ssn = ssn;
		this.balance = 1;
		this.accountType = accountType;
		this.accountPasswd = accountPasswd;
		this.lastTradingDate = LocalDate.now();
		this.accountMaturity = accountMaturity;
		this.accountDueDate = accountDueDate;
		
		if (accountType.equals("정기 예금")) {
			this.depositInterestRate = 2.4;
		} else if (accountType.equals("정기 적금")) {
			this.depositInterestRate = 3.1;
		} else {
			this.depositInterestRate = 3.3;
		}
		
	}

	// getter and setter
	public int getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(int creditScore) {
		this.creditScore = creditScore;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getAccountPasswd() {
		return accountPasswd;
	}

	public void setAccountPasswd(String accountPasswd) {
		this.accountPasswd = accountPasswd;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getBalance() {
		return balance;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public LocalDate getLastTradingDate() {
		return lastTradingDate;
	}

	public void setLastTradingDate(LocalDate lastTradingDate) {
		this.lastTradingDate = lastTradingDate;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	public boolean isFreez() {
		return isFreezing;
	}
	
	/*
	 *메소드명: setFreez
	 *파라미터: boolean isFreez
	 *isFreez: true라면 계좌 동결, false라면 계좌 동결해제
	 *반환값: void
	 *기능 설명: 
	 *isFreez값에 따라서 isFreezing에 true 또는 false를 넣어주고
	 *이체제한(isTransferLimiting) 또는 출금제한(isWithLimiting) 상태에 따라서
	 *state의 값을 변경해준다.
	 */
	
	public void setFreez(boolean isFreez) {
		this.isFreezing = isFreez;
		
		if (isFreez) {
			state = 1;
			if (isTransferLimiting && isWithLimiting) {
				state = 8;
			} else if (isTransferLimiting) {
				state = 7;
			} else if (isWithLimiting) {
				state = 6;
			}
		} else {
			state = 0;
			if (isTransferLimiting && isWithLimiting) {
				state = 5;
			} else if (isTransferLimiting) {
				state = 4;
			} else if (isWithLimiting) {
				state = 3;
			}
		}
		
	}



	public boolean isNonTradingAcc() {
		return isNonTradingAccing;
	}



	public void setNonTradingAcc(boolean isNonTradingAccing) {
		this.isNonTradingAccing = isNonTradingAccing;
	}



	public boolean isWithLimit() {
		return isWithLimiting;
	}



	public void setWithdrawalLimit(boolean isWithLimiting) {
		this.isWithLimiting = isWithLimiting;
	}



	public boolean isTransferLimit() {
		return isTransferLimiting;
	}



	public void setTransferLimit(boolean isTransferLimiting) {
		this.isTransferLimiting = isTransferLimiting;
	}
	
	public long getTransferLimit() {
		return transferLimit;
	}
	
	public void setTransferLimit(long transferLimits) {
		this.transferLimit += transferLimits;		
		}

	public long getWithdrawalLimit() {
		return withdrawalLimit;
	}
	
	
	public void setWithdrawalLimit(long withdrawalLimits) 
	{
		this.withdrawalLimit += withdrawalLimits;
	}



	public LocalDateTime getFreezTime() {
		return freezTime;
	}


	public void setFreezTime(LocalDateTime freezTime) {
		this.freezTime = freezTime;
	}
	
	public long getLoanLimit() {
		return loanLimit;
	}

	public void setLoanLimit(long loanLimit) {
		this.loanLimit = loanLimit;
	}

	public long getPrincipal() {
		return principal;
	}

	public void setPrincipal(long principal) {
		this.principal = principal;
	}

	public int getRepaymentdate() {
		return repaymentdate;
	}
	
//	// 매게변수가 int로 들어오면 필드값이 다음달 매게변수일로 저장됩니다.
//	public void setRepaymentdate(int repaymentdate) {
//		LocalDate nextRepaymentdate = LocalDate.now();
//		nextRepaymentdate = nextRepaymentdate.plusMonths(1);
//		this.repaymentdate = LocalDate.of(nextRepaymentdate.getYear(), nextRepaymentdate.getMonth(), repaymentdate);
//
//	}
	
	// 매게변수가 LocalDate으로 들어오면 필드값이 매게변수값으로 설정됩니다. 
	public void setRepaymentdate(int repaymentdate) {
		this.repaymentdate = repaymentdate;
	}
	
	public int getRepaymentperiod() {
		return repaymentperiod;
	}

	public void setRepaymentperiod(int repaymentperiod) {
		this.repaymentperiod = repaymentperiod;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public double getInterestPerMonth() {
		return interestPerMonth;
	}

	public void setInterestPerMonth(double interestPerMonth) {
		this.interestPerMonth = interestPerMonth;
	}

	public long getPrincipalPerMonth() {
		return principalPerMonth;
	}

	public void setPrincipalPerMonth(long principalPerMonth) {
		this.principalPerMonth = principalPerMonth;
	}

	public long getPrincipalAndInterestPerMonth() {
		return principalAndInterestPerMonth;
	}

	public void setPrincipalAndInterestPerMonth(long principalAndInterestPerMonth) {
		this.principalAndInterestPerMonth = principalAndInterestPerMonth;
	}
	public long getPrincipalAndInterest() {
		return principalAndInterest;
	}

	public void setPrincipalAndInterest(long principalAndInterest) {
		this.principalAndInterest = principalAndInterest;
	}
	
	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public LocalDate getAccountMaturity() {
		return accountMaturity;
	}

	public void setAccountMaturity(LocalDate accountMaturity) {
		this.accountMaturity = accountMaturity;
	}

	public LocalDate getAccountDueDate() {
		return accountDueDate;
	}

	public void setAccountDueDate(LocalDate accountDueDate) {
		this.accountDueDate = accountDueDate;
	}

	public double getDepositInterestRate() {
		return depositInterestRate;
	}

	public void setDepositInterestRate(double depositInterestRate) {
		this.depositInterestRate = depositInterestRate;
	}

	public double getTimeInterestRate() {
		return timeInterestRate;
	}

	public void setTimeInterestRate(double timeInterestRate) {
		this.timeInterestRate = timeInterestRate;
	}

	public double getSavingInterestRate() {
		return savingInterestRate;
	}

	public void setSavingInterestRate(double savingInterestRate) {
		this.savingInterestRate = savingInterestRate;
	}

	public double getFreeInterestRate() {
		return freeInterestRate;
	}

	public void setFreeInterestRate(double freeInterestRate) {
		this.freeInterestRate = freeInterestRate;
	}

	//메소드
	//계좌 상태 변경용 메소드
	/*
	 * 메소드명: accountStatus
	 * 파라미터: int status
	 * status: 계좌 상태를 입력받는다.
	 * 반환값: void
	 * 기능 설명: 
	 * 
	 * status의 숫자에 따라 상태를 변경함
		   0 : 정상 
		   1 : 계좌 동결 
		   2 : 휴면 계좌 
		   3 : 출금 제한
		   4 : 이체 제한
		   5 : 이체,출금 제한
		   6 : 출금,계좌 동결
		   7 : 이체,계좌 동결
		   8 : 이체,출금,계좌 동결
	 * 
	 * 
	 */
	public void setAccountStatus(int status) {
		
		switch (status) {
		case 0:
			isFreezing = false;
			isNonTradingAccing = false;
			isWithLimiting = false;
			isTransferLimiting = false;
			break;

		case 1:
			isFreezing = true;
			break;
			
		case 2:
			isNonTradingAccing = true;
			break;
			
		case 3:
			isWithLimiting = true;
			break;
			
		case 4:
			isTransferLimiting = true;
			break;
			
		case 5:
			isWithLimiting = true;
			isTransferLimiting = true;
			break;
		
		case 6:
			isWithLimiting = true;
			isFreezing = true;
			break;
		
		case 7:
			isTransferLimiting = true;
			isFreezing = true;
			break;
			
		case 8:
			isWithLimiting = true;
			isTransferLimiting = true;
			isFreezing = true;
			break;
		}
		
	}
}
