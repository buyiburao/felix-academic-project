function submit_result() {
    user = document.getElementById("user").value;
    if (user == "") {
        alert("Please log in");
        window.location = "login.jsp";
    } else {
        form = document.createElement("form");
        form.action = "results.jsp";
        form.method = "POST";
		
        input = document.createElement("input");
        input.type = "hidden";
        input.name = "user";
        input.value = user;
        form.appendChild(input);
		
        input = document.createElement("input");
        input.type = "hidden";
        input.name = "query";
        input.value = document.getElementById("query").value;
        form.appendChild(input);
		
        input = document.createElement("input");
        input.type = "hidden";
        input.name = "url";
        input.value = document.getElementById("url").value;
        form.appendChild(input);
		
        results = document.getElementById("results").getElementsByTagName("div");
        for (i = 0; i < results.length; ++i) {
            result = results[i];
			
            input = document.createElement("input");
            input.type = "hidden";
            input.name = "r_" + i;
            input.value = result.title;
			
            form.appendChild(input);
        }
		
        document.body.appendChild(form);
        form.submit();
    }
}

function click_result() {
    results = document.getElementById("results").getElementsByTagName("div");
    for (i = 0; i < results.length; ++i) {
        result = results[i];
        result.className = "sentence";
    }
	
    this.className = "selecting sentence";
}


function select() {
    sentences = document.getElementById("sentences").getElementsByTagName("div");
    for (i = 0; i < sentences.length; ++i) {
        sentence = sentences[i];
        if (sentence.className == "selecting sentence") {
            sentence.className = "selected sentence";
			
            d = document.createElement("div");
            d.innerHTML = sentence.innerHTML;
            d.title = sentence.title;
            d.className = "sentence";
            d.onclick = click_result;
			
            document.getElementById("results").appendChild(d);
        }
    }
}

function result_remove() {
    results = document.getElementById("results").getElementsByTagName("div");
    for (i = 0; i < results.length; ++i) {
        result = results[i];
		
        if (result.className == "selecting sentence") {
            buf = result.innerHTML;
            document.getElementById("results").removeChild(result);
			
            sentences = document.getElementById("sentences").getElementsByTagName("div");
            for (j = 0; j < sentences.length; ++j) {
                sentence = sentences[j];
                if (sentence.innerHTML == buf) {
                    sentence.className = "sentence";
                }
            }
			
            break;
        }
    }
}

function result_move(delta) {
    results = document.getElementById("results").getElementsByTagName("div");
    for (i = 0; i < results.length; ++i) {
        result = results[i];
		
        if (result.className == "selecting sentence") {
            if (i + delta < 0 || i + delta >= results.length) {
                continue;
            }
			
            buf = result.innerHTML;
            result.innerHTML = results[i + delta].innerHTML;
            results[i + delta].innerHTML = buf;
			
            result.className = "sentence";
            results[i + delta].className = "selecting sentence";
			
            break;
        }
    }
}

function result_move_up() {
    result_move(-1);
}

function result_move_down() {
    result_move(1);
}

function click_sentence() {
    if (this.className == "selecting sentence") {
        this.className = "sentence";
    } else if (this.className == "sentence") {
        this.className = "selecting sentence";
    }
}

function translate(sentence, func) {
    google.language.translate(sentence, "en", "zh-CN", func);
}

function decorate(sentence, query) {
//    sentence = " " + sentence + " ";
//    terms = query.split(" ");
//    for (i = 0; i < terms.length; ++i) {
//        term = terms[i];
//        reg = new RegExp(" " + term + " ", "gm");
//        sentence = sentence.replace(reg, " <b>" + term + "</b> ");
//    }
    return sentence;
}

function load_doc() {
    query = document.getElementById("query").value;
    sentences = document.getElementById("sentences").getElementsByTagName("div");
    for (i = 0; i < sentences.length; ++i) {
        sentence = sentences[i];
        sen_en = sentence.innerHTML;
        sentence.innerHTML = decorate(sen_en, query);
        sen_cn = sentence.title;
        sentence.title = sen_en;
        sentence.appendChild(document.createElement("br"));
        chn = document.createElement("span");
        chn.className = "chinese";
        chn.innerHTML = sen_cn;
        sentence.appendChild(chn);
		
        sentence.onclick = click_sentence;
    }

    select();
}

function on_load() {
    document.getElementById("btnSelect").onclick = select;
    document.getElementById("btnUp").onclick = result_move_up;
    document.getElementById("btnDown").onclick = result_move_down;
    document.getElementById("btnRemove").onclick = result_remove;
    document.getElementById("ok").onclick = submit_result;
	
    google.language.getBranding("google_branding");

    translate(document.getElementById("query_en").innerHTML, function(result) {
        document.getElementById("query_cn").innerHTML = result.translation;
    });
	
    translate(document.getElementById("title_en").innerHTML, function(result) {
        document.getElementById("title_cn").innerHTML = result.translation;
    });

    load_doc();
}

google.load("language", "1");
google.setOnLoadCallback(on_load);
