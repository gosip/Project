package helpers;

import helpers.questions.QuestionHTML;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import db.DBConnection;

public class Quiz {
	
	protected int id;
	protected String name;
	protected String descript;
	protected String creation_time;
	protected int creator_id;
	protected boolean one_page;
	protected boolean shuffle;
	protected static DBConnection db;
	
	private ArrayList<QuestionHTML> questions;
	
	public static void setDB(DBConnection connection){
		db = connection;
	}
	
	// Constructor used while creating a new quiz by user
	public Quiz(String name, String descript, boolean one_page, int creator_id, boolean shuffle) {
		init(name, descript, one_page, creator_id, shuffle);
	}
	
	public Quiz(ResultSet rs) throws SQLException {
		init(rs.getString("quizzes.name"), rs.getString("quizzes.descript"), rs.getBoolean("quizzes.one_page"), rs.getInt("quizzes.creator_id"), rs.getBoolean("quizzes.shuffle"));
		this.id = rs.getInt("id");
	}
	
	public Quiz(int id){
		if(db == null) db = new DBConnection();
		Statement stat = (Statement) db.getStatement();
		ResultSet rs = null;
		try {
			rs = stat.executeQuery("SELECT * FROM quizzes WHERE id = \"" + id + "\"");
			init(rs.getString("name"), rs.getString("descript"), rs.getBoolean("one_page"), rs.getInt("creator_id"), rs.getBoolean("shuffle"));
			this.id = rs.getInt("id");
			getQuestionsFromDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try{ stat.close(); } catch(SQLException ignored) {}
		}
	}
	
	private void getQuestionsFromDB() {
		// fill in question array list with questionHTMLs
		if(db == null) db = new DBConnection();
		Statement stat = (Statement) db.getStatement();
		ResultSet rs = null;
		try {
			rs = stat.executeQuery("SELECT * FROM questions JOIN question_types "
					+ "ON questions.type=question_types.id WHERE questions.quiz_id=" + this.id);
			while(rs.next()) {
				questions.add(QuestionHTML.wrap(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try{ stat.close(); } catch(SQLException ignored) {}
		}
	}
	
	private void init(String name, String descript, boolean one_page, int creator_id, boolean shuffle){
		questions = new ArrayList<QuestionHTML>();
		this.name = name;
		this.descript = descript;
		this.one_page = one_page;
		this.creator_id = creator_id;
		this.shuffle = shuffle;
	}
	
	public boolean addToDB() {
		// ???
		if(db == null) db = new DBConnection();
		Statement stat = (Statement) db.getStatement();
		ResultSet rs = null;
		try{
			stat.executeUpdate("INSERT INTO quizzes(name, descript, creator_id, one_page, shuffle) "
					+ "VALUES('" + name + "', '" + descript + "', " + creator_id + 
					", " + one_page + ", " + shuffle + ");");
			rs = stat.executeQuery("SELECT id FROM quizzes WHERE creator_id=" + creator_id + 
					" ORDER BY id DESC LIMIT 1;");
			rs.next();
			id = rs.getInt(1);
			return true;
		}catch(SQLException e){ return false; }
		finally{
			try{ stat.close(); } catch(SQLException ignored) {}
		}
	}

	public void addQuestion(QuestionHTML qu) {
		questions.add(qu);
	}
	
	public int getQuestionNum() {
		return questions.size();
	}
	
	public void shuffle() {
		if(!shuffle) return;
		Random rgen = new Random();
		for(int i = 0; i < questions.size(); i++){
			int rand = rgen.nextInt(questions.size());
			QuestionHTML temp = questions.get(rand);
			questions.set(rand, questions.get(i));
			questions.set(i, temp);
		}
	}
	
	public Iterator<QuestionHTML> getQuestions() {
		return questions.iterator();
	}
	
	public String getName() {
		return name;
	}
	
	//defined only for objects created with the RS constructor
	public int getID() { 
		return id;
	}
	
	public String getDescription() { 
		return descript;
	}
	
	public String getCreationTime() {
		return creation_time;
	}
	
	public boolean onePage() {
		return one_page;
	}
	
	public int getCreatorID(){
		return creator_id;
	}
	
}
