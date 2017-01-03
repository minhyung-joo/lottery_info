package lottery_info;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LotteryParser {
	final static String BASE_PATH = "http://www.sajuforum.com/01forum/lotto/lotto_winnum.php";
	final static String FILE_PATH = "C:/Users/Minhyung Joo/workspace/lottery_info/excel/";
	
	URL url;
	HttpURLConnection con;
	
	ArrayDeque<Integer> firstNumbers;
	ArrayDeque<Integer> secondNumbers;
	ArrayDeque<Integer> thirdNumbers;
	ArrayDeque<Integer> fourthNumbers;
	ArrayDeque<Integer> fifthNumbers;
	ArrayDeque<Integer> sixthNumbers;
	ArrayDeque<Integer> bonusNumbers;
	
	public LotteryParser() {
		firstNumbers = new ArrayDeque<Integer>();
		secondNumbers = new ArrayDeque<Integer>();
		thirdNumbers = new ArrayDeque<Integer>();
		fourthNumbers = new ArrayDeque<Integer>();
		fifthNumbers = new ArrayDeque<Integer>();
		sixthNumbers = new ArrayDeque<Integer>();
		bonusNumbers = new ArrayDeque<Integer>();
	}
	
	public String openConnection(String path, String method, String param) throws IOException {
		url = new URL(path);
		con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
		
		if (method.equals("POST")) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(param);
			wr.flush();
			wr.close();
		}
		int responseCode = con.getResponseCode();
		
		System.out.println(path);
		System.out.println("Response Code: " + responseCode);
		System.out.println();
		
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			
			return response.toString();
		}
		else return null;
	}
	
	public void getNumbers() throws IOException {
		String path = LotteryParser.BASE_PATH;
		
		String html = openConnection(path, "GET", null);
		Document doc = Jsoup.parse(html);
		Element infoTable = doc.getElementsByTag("table").get(5);
		Elements rows = infoTable.getElementsByTag("tr");
		
		int page = 1;
		while(rows.size() > 1) {
			for (int i = 1; i < rows.size(); i++) {
				Element row = rows.get(i);
				Elements data = row.getElementsByTag("td");
				
				String first = data.get(1).text();
				if (first.equals("")) {
					first = data.get(1).child(0).attr("src").replaceAll("[^\\d]", "");
					String second = data.get(2).child(0).attr("src").replaceAll("[^\\d]", "");
					String third = data.get(3).child(0).attr("src").replaceAll("[^\\d]", "");
					String fourth = data.get(4).child(0).attr("src").replaceAll("[^\\d]", "");
					String fifth = data.get(5).child(0).attr("src").replaceAll("[^\\d]", "");
					String sixth = data.get(6).child(0).attr("src").replaceAll("[^\\d]", "");
					String bonus = data.get(7).child(0).attr("src").replaceAll("[^\\d]", "");
					
					firstNumbers.push(new Integer(first));
					secondNumbers.push(new Integer(second));
					thirdNumbers.push(new Integer(third));
					fourthNumbers.push(new Integer(fourth));
					fifthNumbers.push(new Integer(fifth));
					sixthNumbers.push(new Integer(sixth));
					bonusNumbers.push(new Integer(bonus));
				}
				else {
					firstNumbers.push(new Integer(first));
					secondNumbers.push(new Integer(data.get(2).text()));
					thirdNumbers.push(new Integer(data.get(3).text()));
					fourthNumbers.push(new Integer(data.get(4).text()));
					fifthNumbers.push(new Integer(data.get(5).text()));
					sixthNumbers.push(new Integer(data.get(6).text()));
					bonusNumbers.push(new Integer(data.get(7).text()));
				}
			}
			
			page++;
			
			path = LotteryParser.BASE_PATH;
			path += "?pages=" + page;
			html = openConnection(path, "GET", null);
			doc = Jsoup.parse(html);
			infoTable = doc.getElementsByTag("table").get(5);
			rows = infoTable.getElementsByTag("tr");
		}
	}

	public void toExcel() throws IOException {
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("Winning combinations");
		
		int cellIndex = 0;
		Row columnNames = sheet.createRow(0);
		columnNames.createCell(cellIndex++).setCellValue("È¸Â÷");
		columnNames.createCell(cellIndex++).setCellValue("1Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("2Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("3Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("4Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("5Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("6Â°ÃßÃ·¼ö");
		columnNames.createCell(cellIndex++).setCellValue("º¸³Ê½º ¼ö");
		
		System.out.println(firstNumbers.size());
		
		int rowIndex = 1;
		int size = firstNumbers.size();
		for (int i = 0; i < size; i++) {
			Row row = sheet.createRow(rowIndex++);
			cellIndex = 0;
			
			row.createCell(cellIndex++).setCellValue(i + 1);
			row.createCell(cellIndex++).setCellValue(firstNumbers.pop());
			row.createCell(cellIndex++).setCellValue(secondNumbers.pop());
			row.createCell(cellIndex++).setCellValue(thirdNumbers.pop());
			row.createCell(cellIndex++).setCellValue(fourthNumbers.pop());
			row.createCell(cellIndex++).setCellValue(fifthNumbers.pop());
			row.createCell(cellIndex++).setCellValue(sixthNumbers.pop());
			row.createCell(cellIndex++).setCellValue(bonusNumbers.pop());
		}
		
		String filePath = LotteryParser.FILE_PATH + "lottery.xls";
		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);
		fos.close();
	}
	
	public static void main(String args[]) throws IOException {
		LotteryParser test = new LotteryParser();
		
		test.getNumbers();
		test.toExcel();
	}
}
