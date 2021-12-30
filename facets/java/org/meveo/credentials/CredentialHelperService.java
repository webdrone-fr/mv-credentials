package org.meveo.credentials;

import java.util.Map;

import org.meveo.service.script.Script;
import org.meveo.admin.exception.BusinessException;
import javax.ws.rs.client.*;

import java.io.IOException;
import java.util.List;
import org.meveo.model.customEntities.Credential;
import org.meveo.model.storage.Repository;
import org.meveo.api.persistence.CrossStorageApi;
import org.meveo.elresolver.ValueExpressionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialHelperService extends Script {
  
  private static final Logger log = LoggerFactory.getLogger(CredentialHelperService.class);

    public static class LoggingFilter implements ClientRequestFilter {
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            if(requestContext!=null){
              if(requestContext.getEntity()!=null){
                log.info(requestContext.getEntity().toString());
              } else {
                log.info("uri:{}",requestContext.getUri());
              }
            }
        }
    }

    public static Credential getCredential(String domain,CrossStorageApi crossStorageApiInstance,Repository repo){
      List<Credential> matchigCredentials = crossStorageApiInstance.find(repo, Credential.class)
                .by("domainName", domain)
				.getResults();
      if(matchigCredentials.size()>0){
        return matchigCredentials.get(0);
      } else {
        return null;
      }
    }
  
    public static Invocation.Builder setCredential(Invocation.Builder invocBuilder,Credential credential) throws BusinessException {
      String headerKey = credential.getHeaderKey();
      String headerValue = credential.getHeaderValue();
      try{
        if(headerKey.contains("#{")){
          headerKey=ValueExpressionWrapper.evaluateToStringMultiVariable(headerKey,"entity",credential);
        }
        if(headerValue.contains("#{")){
          headerValue=ValueExpressionWrapper.evaluateToStringMultiVariable(headerValue,"entity",credential);
        }
      } catch(Exception e) {
        throw new BusinessException(e);
      }
      return invocBuilder.header(headerKey, headerValue);
    }
  
	@Override
	public void execute(Map<String, Object> parameters) throws BusinessException {
    }
	
}