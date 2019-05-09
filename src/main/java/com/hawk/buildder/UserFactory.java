package com.hawk.buildder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class UserFactory {

    public static void main(String[] args) {
        User user = UserFactory.builder()
                            .name("hawk")
                            .password("123")
                            .sex("M")
                            .age(31);
        System.out.println("user = " + JSONObject.toJSONString(user));

    }
    public static User builder(){
        return new User();
    }
    static class User{
        private String name;
        private String password;
        private String sex;
        private Integer age;

        public User name(String name) {
            this.name = name;
            return this;
        }
        public User password(String password) {
            this.password = password;
            return this;
        }
        public User sex(String sex) {
            this.sex = sex;
            return this;
        }

        public User age(Integer age) {
            this.age = age;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
