package com.mycompany.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.webapp.aspect.LoginChecking401;
import com.mycompany.webapp.dto.CartitemJoinProduct;
import com.mycompany.webapp.dto.Member;
import com.mycompany.webapp.dto.Order;
import com.mycompany.webapp.dto.Orderitem;
import com.mycompany.webapp.dto.OrderitemJoinProduct;
import com.mycompany.webapp.dto.Product;
import com.mycompany.webapp.exception.OutOfStockExceptionHandler;
import com.mycompany.webapp.service.CartitemService;
import com.mycompany.webapp.service.ListviewService;
import com.mycompany.webapp.service.MemberService;
import com.mycompany.webapp.service.OrderService;
import com.mycompany.webapp.service.OrderitemService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/order")
public class OrderController {

	@Resource
	private MemberService memberService;
	@Resource
	private OrderService orderService;
	@Resource
	private OrderitemService orderitemService;
	@Resource
	private CartitemService cartitemService;
	@Resource
	private ListviewService productService;

	// 장바구니에서 선택한 상품 주문하기
	@LoginChecking401
	@RequestMapping("/orderPage")
	public String orderPage(
			@RequestParam(value="orderPcode") ArrayList<String> orderPcode,
			@RequestParam(value="orderPimage1") ArrayList<String> orderPimage1,
			@RequestParam(value="orderPcolorimage") ArrayList<String> orderPcolorimage,
			@RequestParam(value="orderPbrand") ArrayList<String> orderPbrand,
			@RequestParam(value="orderPname") ArrayList<String> orderPname,
			@RequestParam(value="orderPcolor") ArrayList<String> orderPcolor,
			@RequestParam(value="orderPsize") ArrayList<String> orderPsize,
			@RequestParam(value="orderPprice") ArrayList<Integer> orderPprice,
			@RequestParam(value="orderPquantity") ArrayList<Integer> orderPquantity,
			@RequestParam(value="isSelected") ArrayList<Integer> isSelected, //0:선택x, 1:선택
//	public String orderPage(
			Model model,
			HttpServletResponse response) throws Exception {
		log.info("Run order/orderPage");
		
		//테스트
//		ArrayList<String> orderPcode = new ArrayList<>();
//		orderPcode.add("CM2B0KCD230W");
//		ArrayList<String> orderPimage1 = new ArrayList<>();
//		orderPimage1.add("http://newmedia.thehandsome.com/CM/2B/SS/CM2B0KCD230W_PK_W01.jpg/dims/resize/684x1032/");
//		ArrayList<String> orderPcolorimage = new ArrayList<>();
//		orderPcolorimage.add("http://newmedia.thehandsome.com/CM/2B/SS/CM2B0KCD230W_PK_C01.jpg");
//		ArrayList<String> orderPbrand = new ArrayList<>();
//		orderPbrand.add("the CASHMERE");
//		ArrayList<String> orderPname = new ArrayList<>();
//		orderPname.add("캐시미어 크롭 니트 가디건");
//		ArrayList<String> orderPcolor = new ArrayList<>();
//		orderPcolor.add("PK");
//		ArrayList<String> orderPsize = new ArrayList<>();
//		orderPsize.add("85");
//		ArrayList<Integer> orderPprice = new ArrayList<>();
//		orderPprice.add(495000);
//		ArrayList<Integer> orderPquantity = new ArrayList<>();
//		orderPquantity.add(1);
//		ArrayList<Integer> isSelected = new ArrayList<>();
//		isSelected.add(1);

		//mid 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String mid = authentication.getName();

		// 장바구니에서 선택한 상품 데이터를 상품 주문 페이지로 전달
		ArrayList<CartitemJoinProduct> cartitemJoinProduct = new ArrayList<CartitemJoinProduct>();
		
		int totalPquantity = 0; //총 수량
		int totalPrice = 0; //총 가격
				
		for(int i=0; i<orderPcode.size(); i++) {
			if(isSelected.get(i) == 1) {
				int stock = productService.selectPquantity(orderPcode.get(i), orderPcolor.get(i), orderPsize.get(i));
				if(orderPquantity.get(i) > stock) {
					throw new OutOfStockExceptionHandler(orderPname.get(i) + " 제품의 재고가 부족합니다.");
				} else {
					totalPquantity += orderPquantity.get(i);
					totalPrice += (orderPprice.get(i) * orderPquantity.get(i));
					
					cartitemJoinProduct.add(new CartitemJoinProduct(
							orderPcode.get(i), orderPimage1.get(i), orderPcolorimage.get(i), orderPbrand.get(i), 
							orderPname.get(i), orderPcolor.get(i), orderPsize.get(i), orderPprice.get(i), orderPquantity.get(i)));
				}
			}
		}
		model.addAttribute("orderProducts", cartitemJoinProduct);
		model.addAttribute("totalPquantity", totalPquantity);
		model.addAttribute("totalPrice", totalPrice);

		// 주문자 정보(Member Table)에서 가져와서 주문자 정보와 배송지 정보로 전달
		Member orderMember = memberService.selectByMid(mid);
		model.addAttribute("orderMember", orderMember);

		return "order/orderPage";
	}

	// 주문페이지에서 주문완료하기(DB 저장)
	@LoginChecking401
	@PostMapping("/orderComplete")
	public String orderComplete(@RequestParam(value="pcode") ArrayList<String> pcode, 
								@RequestParam(value="pcolor") ArrayList<String> pcolor, 
								@RequestParam(value="psize") ArrayList<String> psize,
								@RequestParam(value="pquantity") ArrayList<Integer> pquantity, 
								String oname,
								String otel,
								String oaddress,
								String ocomment,
								String opaymentmethod,
								HttpSession session) {
		log.info("Run order/orderComplete");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String mid = authentication.getName();
		long oidTime = System.currentTimeMillis();
		String oid = mid + oidTime;
		session.setAttribute("oid", oid);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Date date = new Date();
		date.setTime(oidTime);
		String otime = simpleDateFormat.format(date);

		// 사용자가 작성한 주문 데이터(Order Table)를 DB에 저장
		Order order = new Order(oid, otime, mid, oname, otel, oaddress, ocomment, opaymentmethod, '1');
		orderService.insertOrder(mid, order, pcode, pcolor, psize, pquantity);

		return "redirect:/order/redirectOrderComplete";
  }
	
	// 주문페이지에서 주문완료하기(뷰 데이터 전송)
	@LoginChecking401
	@GetMapping("/redirectOrderComplete")
	public String orderComplete(HttpSession session, Model model) {
		log.info("Run order/redirectOrderComplete");
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String mid = authentication.getName();
		
//		String oid = session.getAttribute("oid").toString();
		String oid = "user1633598522169";
		
		List<Orderitem> orders = orderService.selectByOid(oid);
		
		ArrayList<OrderitemJoinProduct> ordereditems = new ArrayList<OrderitemJoinProduct>();
		int totalprice = 0;		//총 주문 비용
		int totalnumber = 0;	//총 주문 삼품 갯수
		
		for(Orderitem orderitem : orders) {
			Product product = productService.selectOne(orderitem.getPcode(), orderitem.getPcolor(),orderitem.getPsize());
			ordereditems.add(new OrderitemJoinProduct(product.getPname(),orderitem.getPquantity(),product.getPimage1(), product.getPcolorimage(),
													  product.getPbrand(),orderitem.getPcolor(),orderitem.getPsize(),
													  orderitem.getPcode(),orderitem.getOid(),product.getPprice()));
			totalnumber += orderitem.getPquantity();
			totalprice += product.getPprice() * orderitem.getPquantity();
		}
		model.addAttribute ("ordereditems", ordereditems);
		model.addAttribute("totalnumber", totalnumber);
		model.addAttribute("totalprice", totalprice);
		
		Member orderMember = memberService.selectByMid(mid);
		model.addAttribute ("orderMember", orderMember);
		
		Order order = orderService.selectOneByOid(oid);
		model.addAttribute ("order", order);
		
		return "order/orderComplete";
  }
  
	@LoginChecking401
	@RequestMapping("/delete")
	public String delete(String oid) {
		log.info("Run order/delete");

		// 주문 상태 0으로 변경
		orderService.update(oid);

		// 재고 복원
		List<Orderitem> orderItems = orderService.selectByOid(oid);
		for (Orderitem orderitem : orderItems) {
			productService.updatePstock(orderitem.getPcode(), orderitem.getPcolor(), orderitem.getPsize(), orderitem.getPquantity());
		}
		return "order/orderDelete";
	}

}
