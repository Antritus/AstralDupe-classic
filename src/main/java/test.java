public class test {
	private static int getDupeAmount(int timesDupe, int cAmount) {
		return ((int) Math.pow(2, timesDupe)*cAmount);
	}
	private static int calculate(int times){
		return (int) Math.pow(1, times);
	}

	private static boolean isNumeric(String str){
		return str != null && str.matches("[0-9.]+");
	}

	public static void main(String args[]){
		int items = 64;
		for (int i = 1; i < 9; i++){
			int times = getDupeAmount(i, items)/64;
			if (times < 1){
				times = 1;
			}
			System.out.println("/dupe "+i+ " | "+ getDupeAmount(i, items) + " | "+ times);
		}
	}
}
