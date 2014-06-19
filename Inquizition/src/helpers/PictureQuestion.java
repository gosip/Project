package helpers;

public class PictureQuestion extends Question {
	private String imageURL;

	public PictureQuestion(String text, int quizID) {
		super(text, quizID);
		super.type = "prq";
	}

	public String getImageURL() {
		return imageURL;
	}
}
