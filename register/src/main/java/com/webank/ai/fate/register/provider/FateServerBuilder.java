package com.webank.ai.fate.register.provider;
import com.webank.ai.fate.register.annotions.RegisterService;
import com.webank.ai.fate.register.common.Constants;
import com.webank.ai.fate.register.common.RouterModel;
import com.webank.ai.fate.register.interfaces.Registry;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import org.apache.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class FateServerBuilder extends ServerBuilder {



    public String getPartyId() {
        return partyId;
    }

    public FateServerBuilder setPartyId(String partyId) {
        this.partyId = partyId;
        return  this;
    }

    public String getVersion() {
        return version;
    }

    public FateServerBuilder setVersion(String version) {
        this.version = version;
        return  this;
    }

    private  String  partyId;

    private  String  version;

    public String getProject() {
        return project;
    }

    public FateServerBuilder setProject(String project) {
        this.project = project;
        return  this;
    }

    public String getEnvironment() {
        return environment;
    }

    public FateServerBuilder setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    private  String  project;

    private String   environment;

    public String getApplication() {
        return application;
    }

    public FateServerBuilder setApplication(String application) {
        this.application = application;

        return  this;
    }

    private  String  application;




    public FateServerBuilder(ServerBuilder serverBuilder){
        this.serverBuilder =  serverBuilder;
    }

    public  static  FateServerBuilder   forNettyServerBuilderAddress(SocketAddress   socketAddress){

       return   new FateServerBuilder( NettyServerBuilder.forAddress(socketAddress));

    }

    ServerBuilder serverBuilder;


    public  FateServerBuilder maxConcurrentCallsPerConnection(int  count){

        if(this.serverBuilder instanceof NettyServerBuilder){

           this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).maxConcurrentCallsPerConnection(count);
        }

        return  this;

    }
    @Override
    public  FateServerBuilder maxInboundMessageSize(int  count){
//
//        if(this.serverBuilder instanceof NettyServerBuilder){
//
//            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).maxInboundMessageSize(count);
//        }
        this.serverBuilder.maxInboundMessageSize(count);
        return  this;

    }

    public  FateServerBuilder sslContext(    SslContext sslContext){

        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).sslContext(sslContext);
        }

        return  this;


    }


    public  FateServerBuilder flowControlWindow(int  count){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).flowControlWindow(count);
        }
        return  this;
    }

    public  FateServerBuilder keepAliveTime(int  count,TimeUnit timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).keepAliveTime(count,timeUnit);
        }
        return  this;
    }

    public  FateServerBuilder keepAliveTimeout(int  count,TimeUnit timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).keepAliveTimeout(count,timeUnit);
        }
        return  this;
    }


    public  FateServerBuilder permitKeepAliveTime(int  count,TimeUnit timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).permitKeepAliveTime(count,timeUnit);
        }
        return  this;
    }

    public  FateServerBuilder permitKeepAliveWithoutCalls(boolean  permit){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).permitKeepAliveWithoutCalls(permit);
        }
        return  this;
    }




//                    .maxInboundMessageSize(32 << 20)
//                .flowControlWindow(32 << 20)
//                .keepAliveTime(6,TimeUnit.MINUTES)
//                .keepAliveTimeout(24, TimeUnit.HOURS)
//                .maxConnectionIdle(1, TimeUnit.HOURS)
//                .permitKeepAliveTime(1, TimeUnit.SECONDS)
//                .permitKeepAliveWithoutCalls(true)
//                .executor((TaskExecutor) applicationContext.getBean("grpcServiceExecutor"))
//            .maxConnectionAge(24, TimeUnit.HOURS)
//                .maxConnectionAgeGrace(24, TimeUnit.HOURS);

    public  FateServerBuilder maxConnectionAge(int  count ,TimeUnit timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){
            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).maxConnectionAge(count,timeUnit);
        }
        return  this;
    }

    public  FateServerBuilder maxConnectionAgeGrace(int  count ,TimeUnit timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){
            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).maxConnectionAgeGrace(count,timeUnit);
        }
        return  this;
    }




    @Override
    public ServerBuilder directExecutor() {

                serverBuilder.directExecutor();
                return this;

    }

    @Override
    public FateServerBuilder executor(@Nullable Executor executor) {
         serverBuilder.executor(executor);
        return this;

    }

    @Override
    public FateServerBuilder addService(ServerServiceDefinition serverServiceDefinition) {

        System.err.println("addService =====111="+serverServiceDefinition);

         prepareRegister(serverServiceDefinition);
         serverBuilder.addService(serverServiceDefinition);
         return  this;
    }

    public  FateServerBuilder maxConnectionIdle(int count,TimeUnit  timeUnit){
        if(this.serverBuilder instanceof NettyServerBuilder){

            this.serverBuilder =  ((NettyServerBuilder)this.serverBuilder).maxConnectionIdle(count,timeUnit);
        }
        return  this;
    }


    public FateServerBuilder addService(ServerServiceDefinition serverServiceDefinition,Class  clazz) {

        System.err.println("addService =====111="+serverServiceDefinition);

        prepareRegister(clazz);
        serverBuilder.addService(serverServiceDefinition);
        return  this;
    }



    @Override
    public FateServerBuilder addService(BindableService bindableService) {

        System.err.println("addService ======"+bindableService);

        prepareRegister(bindableService);
        serverBuilder.addService(bindableService);
        return this;
    }


    private  void  prepareRegister (Object  service){
        Method[] methods;
        if(service instanceof  Class){
            methods=((Class) service).getMethods();
        }
        else{
            methods= service.getClass().getMethods();
        }

        for(Method method: methods) {
            doRegister(method);
        }

    }


    private   void  doRegister(Method method){

        RegisterService registerService = method.getAnnotation(RegisterService.class);

        if (registerService != null) {


            FateServer.serviceSets.add(registerService);

        }
    }

    @Override
    public ServerBuilder fallbackHandlerRegistry(@Nullable HandlerRegistry handlerRegistry) {
         serverBuilder.fallbackHandlerRegistry(handlerRegistry);
         return  this;
    }

    @Override
    public ServerBuilder useTransportSecurity(File file, File file1) {
        serverBuilder.useTransportSecurity(file,file1);
        return this;
    }

    @Override
    public ServerBuilder decompressorRegistry(@Nullable DecompressorRegistry decompressorRegistry) {
        serverBuilder.decompressorRegistry(decompressorRegistry);
        return this;
    }

    @Override
    public ServerBuilder compressorRegistry(@Nullable CompressorRegistry compressorRegistry) {
        serverBuilder.compressorRegistry(compressorRegistry);
        return this;
    }


    public String getZkRegisterLocation() {
        return zkRegisterLocation;
    }

    public FateServerBuilder setZkRegisterLocation(String zkRegisterLocation) {
        this.zkRegisterLocation = zkRegisterLocation;

        return this;
    }

    private  String  zkRegisterLocation;

    @Override
    public Server build() {

        Server server = serverBuilder.build();

        FateServer fateServer = new FateServer(server);

        fateServer.setEnvironment(environment);
        fateServer.setProject(project);

        return  fateServer;
    }



}
