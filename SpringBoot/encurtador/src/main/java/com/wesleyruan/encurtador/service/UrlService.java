package com.wesleyruan.encurtador.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.wesleyruan.encurtador.model.UrlModel;
import com.wesleyruan.encurtador.repository.UrlRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    

    public ResponseEntity<UrlModel> shortenUrl(String original_url, HttpServletRequest request){
        String code = UUID.randomUUID().toString().substring(0, 6);

        
        UrlModel urlModel = new UrlModel(original_url);
        urlModel.setNewUrl(this.getBaseUrl(request) + "/r/" + code);
        urlModel.setCodeUrl(code);

        UrlModel url = urlRepository.save(urlModel);
        
        if (isValidUrl(urlModel.getOriginalUrl())){
            return ResponseEntity.status(HttpStatus.CREATED).body(url);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    public String getRedirectUrl(String codeUrl) {
        return urlRepository.findByCodeUrl(codeUrl)
                            .map(UrlModel::getOriginalUrl)
                            .orElse(null); // retorna null se não existir
    }

    private boolean isValidUrl(String url){
        try{
            new URL(url).toURI();
            return true;
        }catch (MalformedURLException e){
            return false;
        }catch (URISyntaxException e){
            return false;
        }
    }

    private String getBaseUrl(HttpServletRequest request){
        String schema = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String baseUrl = schema + "://" + serverName + ((serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort);

        return baseUrl;


    }


}
