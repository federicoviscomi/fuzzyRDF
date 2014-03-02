package fuzzyRDFSEngine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.text.TabExpander;

public class SQLUtils {

	public static void printAll(ResultSet tableContent, PrintWriter out) {
		try {
			ResultSetMetaData metaData = tableContent.getMetaData();
			int columnCount = metaData.getColumnCount();
			String format = "%70.70s   ";
			String format1 = "%70.70s | ";
			out.print("\n| ");
			for (int i = 1; i <= columnCount; i++) {
				out.format(format, "");
			}
			out.print("|\n| " + metaData.getTableName(1) + "\n| ");
			for (int i = 1; i <= columnCount; i++) {
				out.format(format1, metaData.getColumnName(i));
			}
			out.print("\n| ");
			for (int i = 1; i <= columnCount; i++) {
				out.format(format1, metaData.getColumnTypeName(i));
			}
			out.print("\n| ");
			for (int i = 1; i <= columnCount; i++) {
				out.format(format1, "");
			}
			while (tableContent.next()) {
				out.print("\n| ");
				for (int i = 1; i <= columnCount; i++) {
					out.format(format1, tableContent.getObject(i));
				}
			}
			out.print("\n| ");
			for (int i = 1; i <= columnCount; i++) {
				out.format(format1, "");
			}
			out.print("|\n");
			out.flush();
			Thread.sleep(1000);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void printAll(ResultSet queryRes, PrintStream out) {
		printAll(queryRes, new PrintWriter(out));
	}

}
