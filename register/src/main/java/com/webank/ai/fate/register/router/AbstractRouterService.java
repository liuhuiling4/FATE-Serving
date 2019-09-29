package com.webank.ai.fate.register.router;

import com.google.common.collect.Lists;
import com.webank.ai.fate.register.common.AbstractRegistry;
import com.webank.ai.fate.register.common.Constants;
import com.webank.ai.fate.register.common.RouterModel;
import com.webank.ai.fate.register.interfaces.Registry;
import com.webank.ai.fate.register.loadbalance.DefaultLoadBalancerFactory;
import com.webank.ai.fate.register.loadbalance.LoadBalanceModel;
import com.webank.ai.fate.register.loadbalance.LoadBalancer;
import com.webank.ai.fate.register.loadbalance.LoadBalancerFactory;
import com.webank.ai.fate.register.url.CollectionUtils;
import com.webank.ai.fate.register.url.URL;
import com.webank.ai.fate.register.utils.StringUtils;


import java.util.List;


public abstract  class AbstractRouterService  implements RouterService {



    protected LoadBalancerFactory   loadBalancerFactory=new DefaultLoadBalancerFactory();

    protected AbstractRegistry registry;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(AbstractRegistry registry) {
        this.registry = registry;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    protected LoadBalancer loadBalancer;


    @Override
    public List<URL> router(URL  url, LoadBalanceModel  loadBalanceModel){

        this.loadBalancer = loadBalancerFactory.getLoaderBalancer(loadBalanceModel);

        return doRouter(url,loadBalanceModel);
    }

    public  abstract List<URL>  doRouter(URL url,LoadBalanceModel  loadBalanceModel);


    @Override
    public List<URL> router(URL url) {
        return  this.router(url,LoadBalanceModel.random);
    }

    protected  List<URL>  filterVersion(List<URL>  urls,String  version){

        final List<URL>   resultUrls = Lists.newArrayList();

        if(CollectionUtils.isNotEmpty(urls)){

            urls.forEach(url-> {

                String targetVersion = url.getParameter(Constants.VERSION_KEY);
                String routerModel = url.getParameter(Constants.ROUTER_MODEL);

                try {
                    Double targetVersionValue = Double.parseDouble(targetVersion);
                    Double versionValue = Double.parseDouble(version);

                    if (targetVersionValue != null && versionValue != null) {
                        if (String.valueOf(RouterModel.VERSION_BIGER).equalsIgnoreCase(routerModel)) {
                            if (versionValue.doubleValue() > targetVersionValue.doubleValue()) {
                                resultUrls.add(url);
                            }
                        } else if (String.valueOf(RouterModel.VERSION_BIGTHAN_OR_EQUAL).equalsIgnoreCase(routerModel)) {
                            if (versionValue.doubleValue() >= targetVersionValue.doubleValue()) {
                                resultUrls.add(url);
                            }
                        } else if (String.valueOf(RouterModel.VERSION_SMALLER).equalsIgnoreCase(routerModel)) {
                            if (versionValue.doubleValue() < targetVersionValue.doubleValue()) {
                                resultUrls.add(url);
                            }
                        } else if (String.valueOf(RouterModel.VERSION_EQUALS).equalsIgnoreCase(routerModel)) {
                            if (versionValue.doubleValue() == targetVersionValue.doubleValue()) {
                                resultUrls.add(url);
                            }
                        } else {
                            resultUrls.add(url);
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("parse version error");
                }
            });
        }
        return  resultUrls;
    }

}
