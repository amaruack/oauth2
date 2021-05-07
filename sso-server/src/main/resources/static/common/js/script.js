$(document).ready(function () {
    var $openBtn = $(".show_hide_arrow");
    var $openiconBtn = $(".bt");
    var $menu = $("#iot");
    var $list = $("#iot > ul > li");
    var $item = $("#iot > ul > li > a");
    var $sublist = $("#iot > ul > li > ul > li");
    var $lastList = $list.filter('.active');


    /* 메뉴 열고 닫기 */
    $openBtn.click(function (e) {
        //menu가 열려있을 때
        if ($(this).hasClass("open")) {
            //menu를 닫는다
            $(this).removeClass("open");
            $menu.removeClass("open");

            $(".menu_hide").hide();
            $("li.bt").removeClass("active");
            //menu가 닫혀있을 때
        } else {
            $(this).addClass("open");
            $menu.addClass("open");
        }
    });

    /* 대메뉴 선택 */
    $list.click(function (e) {
        //e.preventDefault();
        var currentList = $(this);
        $lastList.removeClass('active');
        currentList.addClass('active');
        $lastList = currentList;
    });

    /* 서브메뉴 선택 */
    $sublist.click(function (e) {
        //e.preventDefault();
        $sublist.removeClass("active");
        $(this).siblings().each(function () {
            $(this).removeClass("active");
        });

        $(this).addClass("active");
    });

    /* 프로필메뉴 popover */
    $('.profileInfo').popover({
        html: true,
        placement: 'bottom',
        content: $('#popover_content')
    });
    $('.profileInfo').popover('show');
    $('.profileInfo').popover('hide');


    /* 어코디언메뉴 */
    // menu 클래스 바로 하위에 있는 a 태그를 클릭했을때
    $(".bt>a").click(function () {
        var submenu = $(this).next("ul");
        // submenu가 화면상에 보일때는 위로 보드랍게 접고 아니면 아래로 보드랍게 펼치기
        if (submenu.is(":visible")) {
            submenu.slideUp();
        } else {
            $("#iot").addClass("open");
            $(".show_hide_arrow").addClass("open");
            $(".bt").each(function () {
                $(this).find("ul").slideUp();
            });
            submenu.slideDown();
        }
    });
});

/* 프로필메뉴 arrow 이미지 토글 */
function arrowC() {
    var arrow = document.getElementById('bul');
    var imgPath;
    if (typeof IMG_PATH !== 'undefined') {
        imgPath = IMG_PATH;
    } else {
        var imgPath = "img";
    }
    console.log("imgPath : ", imgPath, arrow.src.match("btn_on"))
    if (arrow.src.match("btn_on")) {
        arrow.src = imgPath + "/btn_off.png";
    } else {
        arrow.src = imgPath + "/btn_on.png";
    }
}

$(function () {
    //셀렉트박스 그리기
    $('.tag_type').selectric();
    $('.search_type').selectric();

    //전체선택 체크박스 클릭
    $("#allCheck").click(function () {
        //만약 전체 선택 체크박스가 체크된 상태일경우
        if ($("#allCheck").prop("checked")) {
            //해당화면에 전체 checkbox들을 체크해준다.
            $("input[type=checkbox]").prop("checked", true);
            // 전체선택 체크박스가 해제된 경우
        }
        else {
            //해당화면에 모든 checkbox들의 체크를 해제시킨다.
            $("input[type=checkbox]").prop("checked", false);
        }
    })
});
