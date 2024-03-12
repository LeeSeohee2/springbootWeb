package com.kh.jpa9.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.jpa9.dto.MemberDTO;
import com.kh.jpa9.entity.Member;
import com.kh.jpa9.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class JpaController {
	
	@Autowired
	private MemberRepository repository;
	
	
	@GetMapping("/")
	public String index() {
		// 메인페이지로 이동을 하는 매핑메서드!
		return "index";
	}
	
	/**
	 * 회원 등록 Form페이지 + 회원 수정 Form  
	 */
	@GetMapping("/jpa/memberWrite")
	public String memberWrite(
			@RequestParam(value="num" ,required=false)
			Integer num,Model model ) {
		
		log.info("memberWrite: {} ",num);
		
		// 만약 num null 아니면 기존 회원이기 때문에 회원 수정 
		//              보여주고 
		//        null 이면 신규회원이기 때문에 회원 등록을 보여준다
		if(num != null) {
			Member member = null;// 데이터베이스 호출!
		}else {
			log.info("null 이네요!");
			
			// 신규 회원 등록 
			model.addAttribute("memberDTO", new MemberDTO());
			model.addAttribute("formTitle", "Registration");
			
		}
		
		return "jpa/memberWriteForm";
	}
	
	@PostMapping("/jpa/memberWrite")
	public String memberWriteOk(MemberDTO memberDTO
							 , Model model) {
		
		try {
			// 등록처리 
			System.out.println(memberDTO.getName());
			System.out.println(memberDTO.getId());
			System.out.println(memberDTO.getPhone());
			System.out.println(memberDTO.getAge());
			
			//1. DTO 변환 ---> Entity
			Member member = memberDTO.toEntity();
			System.out.println(member);
			
			// 2. Repository로 ---> Entity ---> DB에 저장
			repository.save(member);
			System.out.println("정상적으로 실행되었습니다.");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// 뷰페이지로 새로운 요청을 한다. 
		return "redirect:/";
		
	}
	
//	@PageableDefault 한 페이지에 보여져야되는 데이터 개수 
	
	// 회원 정보 리스트 
	@GetMapping("/jpa/memberList")
	public String memberList1(Model model,
			@PageableDefault(size = 10) Pageable pageable,
			@RequestParam(value="searchCate"
						 ,required = false
						 ,defaultValue="") String searchCate,
			@RequestParam(value="searchKeyword"
			 			 ,required = false
			 			 ,defaultValue="") String searchKeyword
			) {
		
		Page<Member> members = null;
		if(searchCate.equals("name")) {
			System.out.println("이름 검색");
			members = repository.findByNameContaining
								(searchKeyword
								, pageable);
			
		}else if(searchCate.equals("id")){
			System.out.println("아이디 검색");
			members = repository.findById(searchKeyword
										, pageable);
			
		}else if(searchCate.equals("phone")){
			System.out.println("휴대폰 검색");
			members = repository.findByPhoneContaining(
										searchKeyword
										, pageable);
			
		}else {
			
			members = repository
					.findByNameContainsOrIdContains
					(searchKeyword,searchKeyword,
							pageable);
		}
				
		
		System.out.println(members);
		model.addAttribute("members",members);
		return "jpa/memberList";
	}
	//회원 상세 페이지 
	@GetMapping("/jpa/memberDetail")
	public String memberDetail(
			@RequestParam(value="num",required=false)
			Integer num, Model model
			) {
		log.info("memberDetail() : {}",num);
		
		// findById로 해당 num에 해당하는 회원을 찾아서 뷰페이지로
		// 전달한다. 
		Member member = repository
					   .findById(num)
					   .orElse(null);
		model.addAttribute("member", member);
		
		
		return "jpa/memberDetail";
	}
	
	// 수정은 위에서 회원등록할 때 같이 저장 
	
	/**
	 * 회원 삭제 
	 * 
	 */
	@GetMapping("/jpa/memberDelete")
	public String memberDelete(
			@RequestParam(value = "num"
					,required=false) Integer num
			) {
		
		log.info("memberDelete() : {}",num);
		repository.deleteById(num);
		
		
		return "redirect:/jpa/memberList";
	}
}
