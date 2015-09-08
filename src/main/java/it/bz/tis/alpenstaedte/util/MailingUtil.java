package it.bz.tis.alpenstaedte.util;

import it.bz.tis.alpenstaedte.Comment;
import it.bz.tis.alpenstaedte.Idea;
import it.bz.tis.alpenstaedte.PipUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
@Service
public class MailingUtil {
	private static final String PIP = "open@tis.bz.it";
	@Autowired
	private MailSender mailSender;
	
	public void sendCreationMail(Idea idea, String[] toMails) {
		if (idea!=null && toMails.length!=0){
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toMails);
			message.setFrom(PIP);
			message.setSubject("New project idea");
			message.setText("A new project idea named "+idea.getName()+" has been created.\r\nTo find out more about it visit http://projectideas.tis.bz.it/#/idea/"+idea.getUuid());
			mailSender.send(message);
		}
	}
	public void sendUpdateMail(Idea idea, String[] toMails) {
		if (idea!=null && toMails.length!=0){
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toMails);
			message.setFrom(PIP);
			message.setSubject("The project ["+idea.getName()+"] has been changed");
			message.setText(idea.getName()+" has been updated. To find out more about it visit: http://projectideas.tis.bz.it/#/idea/"+idea.getUuid());
			mailSender.send(message);
		}
	}
	public void sendCommentMail(Comment comment, String[] toMails) {
		if (comment!=null && toMails.length!=0){
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(toMails);
			message.setFrom(PIP);
			message.setSubject("["+comment.getIdea().getName()+"] New comment by "+comment.getOwner().getName());
			message.setText("The project idea \"" + comment.getIdea().getName()
					+ "\" has been modified.\r\n"
					+ comment.getOwner().getName()+" "+comment.getOwner().getSurname() 
					+ " has created a new comment:\r\n\""+comment.getText()+"\"\r\n"
					+"To see the complete project idea visit: http://projectideas.tis.bz.it/#/idea/"+comment.getIdea().getUuid());
			mailSender.send(message);
		}
	}
	public void sendCreationMail(PipUser user, String randomPassword) {
		if (user!=null && randomPassword!=null && !randomPassword.isEmpty() ){
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setFrom(PIP);
			message.setSubject("[Project ideas] Account");
			String body = "Welcome to PIP(Project idea platform).\r\nYou can login with your email and this password: "+randomPassword+"\r\nDon't forget to reset your password once you visit\r\nhttp://projectideas.tis.bz.it/";
			message.setText(body);
			mailSender.send(message);
		}
	}
}
