package system;

import java.time.LocalDate;
import java.util.ArrayList;

public class SystemDeadlineCalculator {
	
	private AccountStorage as;
	
	
	public SystemDeadlineCalculator(AccountStorage as) {
		this.as = as;
		computeTermDates();
	}
	
	
	private void computeTermDates() {
		
		ArrayList<ArrayList<Account>> ArrayLists = new ArrayList<ArrayList<Account>>();//AccountStorage안의 ArrayList를 담을 변수
		boolean isMapClear = false; //map들을 전부 지웠을때(ArrayList안에 정기적금,예금,자유적금)계좌가 일반 예금으로 변할때
		
		int index = 0;
		
		
		ArrayLists.add(as.getDepositAccountList());
		ArrayLists.add(as.getSavingAccountList());
		ArrayLists.add(as.getTimeAccountList());
		ArrayLists.add(as.getFreeAccountList());
		
		for (int i = 1; i < ArrayLists.size(); i++) {
			ArrayList<Account> list = ArrayLists.get(i);
			index = 0;
			
			for (int j = 0; j < list.size(); j++) {
				LocalDate now = LocalDate.now();
				Account account = list.get(j);
				//납기일이 오늘보다 전이거나 오늘일때 실행
				if (account.getAccountDueDate().isBefore(now) || account.getAccountDueDate().isEqual(now)) {
					account.setAccountDueDate(account.getAccountDueDate().plusMonths(1));
				}
				
				// 만기일이 오늘보다 전이거나 오늘일때 실행
				if (account.getAccountMaturity().isBefore(now) || account.getAccountMaturity().isEqual(now)) {
					
					isMapClear = true;
					
					String accountNumber = "012";
					accountNumber += account.getAccountNumber().substring(3);
					account.setAccountNumber(accountNumber);
					account.setHomeAddress("관리자에게 문의 하세요");
					account.setPhoneNumber("관리자에게 문의 하세요");
					account.setAccountType("일반 예금");
					account.setDepositInterestRate(0.1);
					
					ArrayLists.get(0).add(account);
					
					as.getMap().clear();
					as.getMultikeyMap().clear();
				}
				
				list.remove(index);
				index++;
			}
			
		}
		
		if (isMapClear) {
			for (int i = 0; i < ArrayLists.size(); i++) {
				ArrayList<Account> list = ArrayLists.get(i);
				for (int j = 0; j < list.size(); j++) {
					as.putMap(list.get(j).getAccountNumber(), j);
					as.putMultikeyMap(list.get(j).getAccountNumber(), list.get(j).getAccountPasswd(), j);
				}
			}	
		}

	}
	
	
}
