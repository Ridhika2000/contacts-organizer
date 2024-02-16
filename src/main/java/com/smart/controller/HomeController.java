package com.smart.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.ConfigMapFetcher;
import com.smart.helper.Message;

import io.fabric8.kubernetes.api.model.ConfigMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Environment environment;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("Register", "About - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@GetMapping("/env")
	public String showEnv(Model model) {
	    try {
	        String namespace = "alpha";
	        String configMapName = "smart-contacts-app-config";

	     // Fetch ConfigMap data using ConfigMapFetcher
            ConfigMap configMap = ConfigMapFetcher.fetchConfigMapData(namespace, configMapName);
            if (configMap != null) {
                // Fetch individual values from the ConfigMap
                String applicationPassword = configMap.getData().get("Application_Password");
                String applicationEnvironment = configMap.getData().get("Application_Environment");
                String applicationName = configMap.getData().get("Application_Name");
                String applicationConnectionString = configMap.getData().get("Application_ConnectionString");
                
                // Add data to the model
    	        model.addAttribute("environment", "Environment - Smart Contact Manager");
    	        model.addAttribute("applicationEnvironment", applicationEnvironment);
    	        model.addAttribute("applicationName", applicationName);
    	        model.addAttribute("applicationPassword", applicationPassword);
    	        model.addAttribute("applicationConnectionString", applicationConnectionString);

                
            } else {
            	 // Add data to the model
    	        model.addAttribute("environment", "Environment - Smart Contact Manager");
    	        model.addAttribute("applicationEnvironment", "default ennvironment");
    	        model.addAttribute("applicationName", "default name");
    	        model.addAttribute("applicationPassword", "default password");
    	        model.addAttribute("applicationConnectionString", "default connectionstring");

            }
	        
	        return "env";
	    } catch (Exception e) {
            // Handle exception (e.g., ConfigMap not found)
            e.printStackTrace();
            model.addAttribute("error", "Error fetching ConfigMap data: " + e.getMessage());
            return "env";
        }
	    
	}

	// handler for registering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			if (!agreement) {
				System.out.println("You have not accepted the terms and conditions");
				throw new Exception("You have not accepted the terms and conditions");
			}

			if (result1.hasErrors()) {
				System.out.println("Error " + result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setAbout(user.getAbout().trim());
			user.setImageUrl("default.png");
			// password encode
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement " + agreement);
			System.out.println("User " + user);
			User result = this.userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	// handler for custom Login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login -Smart Contact Manager");
		return "login";
	}
}
