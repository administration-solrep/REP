package fr.dila.reponses.api.extraction;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExtractionQuestionsResponse {
    private String type;

    private String source;

    private List<QuestionJAXB> question = new ArrayList<>();

    public ExtractionQuestionsResponse() {
        super();
    }

    public ExtractionQuestionsResponse(String type, String source, List<QuestionJAXB> question) {
        super();
        this.type = type;
        this.source = source;
        this.question = question;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<QuestionJAXB> getQuestion() {
        return question;
    }

    public void setQuestion(List<QuestionJAXB> question) {
        this.question = question;
    }
}
