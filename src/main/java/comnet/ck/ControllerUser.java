package comnet.ck;

import java.security.Principal;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControllerUser {
	@Autowired
	private UserRepository repo;
	@Autowired
	private NoteRepository note1;
	@Autowired
	private NoteSevice note2;

	@GetMapping("")
	public String home() {
		return "homepage";
	}

	@GetMapping("/register")
	public String showSignUpForm(Model model) {
		model.addAttribute("user", new User());
		return "signup_form";
	}

	@PostMapping("/process_register")
	public String processRegistration(User user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		repo.save(user);
		return "register_success";
	}

	@GetMapping("/list_users")
	public String viewUsersList(Model model) {
		List<User> listUsers = repo.findAll();
		model.addAttribute("listUsers", listUsers);
		return "users";
	}

	
	@GetMapping("/takenote")
	public String showNotes(Model model) {
		model.addAttribute("note", new Notes());
		return "addNote";
	}
	
	@PostMapping("/take_notes")	
	public String takeNoteSuccess(Notes note, Model model, Principal principal) {
		User user = repo.findByEmail(principal.getName());
		note.setUser(user);
		note1.save(note);
		return "notesuccess";
	}
	@RequestMapping("/takenotes")
	public String viewNotes(Model model, @Param("keyword1") String keyword) {
		List<Notes> notes = note2.listAll(keyword);
		model.addAttribute("takenote1", notes);
		return "takenote";
	}
	@RequestMapping(value="/takenotes/filter")
	public String filterNotes(Model model, @RequestParam("user_id") Long userId) {
		User user = repo.findById(userId).get(); 
		List<Notes> notes = note1.findByUser(user);
		model.addAttribute("takenote1", notes);
		return "takenote";
	}
	@RequestMapping("/deleted/{stt}")
	public String deleteNotes(@PathVariable("stt") Long stt, Model model) {
		note1.deleteById(stt);
		List<Notes> listnote = note1.findAll();
		model.addAttribute("takenote1", listnote);
		return "takenote";
	}
	@RequestMapping("/edit/{stt}")
	public String showEdit(@PathVariable("stt") Long stt,Model model) {
		model.addAttribute("note", note1.findById(stt).orElse(null));
		return "edit";
	}
	
	@RequestMapping("/notes/update")
	public String update(@Param("keyword1") String keyword,Model model,@RequestParam Long stt, @RequestParam String title, @RequestParam String content, @RequestParam Date ngaytao) {
		Notes note = note1.findById(stt).orElse(null);
		note.setTieude(title);
		note.setNoidung(content);
		note.setNgaytao(ngaytao);
		note1.save(note);
		List<Notes> notes = note2.listAll(keyword);
		model.addAttribute("takenote1", notes);
		return "takenote";
	}
	
	
}