package system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.MultiKeyMap;

public class AccountStorage {
	
	// 계좌에 관한 모든 정보를 가진 객체를 저장하는 ArrayList
	private ArrayList<Account> depositAccountlist = new ArrayList<Account>(); 
	private ArrayList<Account> timeAccountlist = new ArrayList<Account>(); 
	private ArrayList<Account> savingAccountlist = new ArrayList<Account>(); 
	private ArrayList<Account> freeAccountlist = new ArrayList<Account>(); 
	
	// 계좌번호와 계좌비밀번호를 키로 가지고 해당 계좌번호가 저장되어있는 list의 인덱스 번호를 값으로 가지고 있는 MultiKeyMap
	private MultiKeyMap<String, Integer> multikeyMap = new MultiKeyMap<String, Integer>(); 
	
	// 계좌번호를 키로 가지고 해당 계좌번호가 저장되어있는 list의 인덱스 번호를 값으로 가지고 있는 HashMap
	private Map<String, Integer> map = new HashMap<String, Integer>(); 
	
	// 계좌번호를 키로가지고 해당 계좌 동결시간을 값으로 가지고 있는 HashMap
	private Map<String, LocalDateTime> freezMap = new HashMap<String, LocalDateTime>(); 
	
	//각 계좌별 리스트에 계좌객체를 추가하는 add메소드 부분
	public void addDepositAccount(Account accountCreate) {
		depositAccountlist.add(accountCreate);
	}
	public void addFreeAccount(Account accountCreate) {
		freeAccountlist.add(accountCreate);
	}
	
	public void addTimeAccount(Account accountCreate) {
		timeAccountlist.add(accountCreate);
	}
	
	public void addSavingAccount(Account accountCreate) {
		savingAccountlist.add(accountCreate);
	}
	
	// index값으로 각 계좌별 리스트에 저장된 계좌를 찾는 get메소드 부분
	public Account getDepositAccount(int index) {
		return depositAccountlist.get(index);
	}
	
	public Account getFreeAccount(int index) {
		return freeAccountlist.get(index);
	}

	public Account getTimeAccount(int index) {
		return timeAccountlist.get(index);
	}
	
	public Account getSavingAccount(int index) {
		return savingAccountlist.get(index);
	}
	
	//맵에 키와 값을 집어넣는 put메소드 부분
	public void putMultikeyMap(String accountNumber, String accountPassword, int index) {
		//계좌 번호(String)과 계좌 비밀번호(String)를 키로하여 list들의 인덱스 번호(int)를 값으로 가진 맵
		multikeyMap.put(accountNumber, accountPassword, index);
	}
	

	public void putMap(String accountNumber, int index) {
		//계좌 번호(String)를 로하여 list들의 인덱스 번호(int)을 값으로 가진 맵
		map.put(accountNumber, index);
	}
	

	public void putFreezMap(String accountNumber, LocalDateTime date) {
		//계좌 번호(String)과 계좌 비밀번호(String)를 키로하여 저장된 시간(LocalDateTime)을 값으로 가진 맵
		freezMap.put(accountNumber, date);
	}
	
	// 맵에서 키로 값을 찾는 find메소드 부분
	public int findAccountNumberFromMultikeyMap(String accountNumber, String accountPassword) {
		//매게변수 계좌 번호(String)과 계좌 비밀번호(String)를 키로하여 list들의 인덱스 번호(int)를 리턴한다(해당하는 키가 없다면 null을 반환한다)
		return multikeyMap.get(accountNumber, accountPassword); 
	}
	
	public int findAccountNumberFromMap(String accountNumber) {
		return map.get(accountNumber);
	}
	public LocalDateTime findAccountFreezTimeFromFreezMap(String accountNumber) {
		return freezMap.get(accountNumber);
	}
	

	
	// getter 메소드들 리스트와 맵을 전체불러온다
	public ArrayList<Account> getDepositAccountList() {
		return depositAccountlist;
	}
	
	public ArrayList<Account> getTimeAccountList() {
		return timeAccountlist;
	}


	public ArrayList<Account> getSavingAccountList() {
		return savingAccountlist;
	}


	public ArrayList<Account> getFreeAccountList() {
		return freeAccountlist;
	}

	
	public Map<String, Integer> getMap() {
		return map;
	}
	
	public MultiKeyMap<String, Integer> getMultikeyMap() {
		return multikeyMap;
	}
	
	public Map<String, LocalDateTime> getFreezMap() {
		return freezMap;
	}
}
