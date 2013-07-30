package dmk.spring.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
 
@Configuration
@ComponentScan(basePackages= { "dmk.pdf" })
@EnableAspectJAutoProxy
public class CacheConfig {
 
	@Bean
	public SimpleCacheManager cacheManager(){
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		ConcurrentMapCache cache = new ConcurrentMapCache("genPdf");
		cacheManager.setCaches(Arrays.asList(cache));
		return cacheManager;
	}

	@Bean 
	public ConcurrentMapCacheFactoryBean cacheFactoryBean(){
		ConcurrentMapCacheFactoryBean cacheFactoryBean = new ConcurrentMapCacheFactoryBean();
//		cacheFactoryBean.setBeanName("cacheFactoryBean");
		cacheFactoryBean.setName("genPdf");
		return cacheFactoryBean;
	}
	
}
