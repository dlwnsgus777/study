## 리팩터링 - 인프런 백기선님 강의 정리 (chapter1 ~ 4)
---


### 리팩터링
- 소프트웨어 행동은 유지하면서 코드를 유지보수하기 쉽게 개선하는 작업
    - 처음부터 완벽하게 시스템을 설계하는 것은 불가능
    - 코드를 변경하는 일이 자주 발생한다
    - 리팩터링을 통해 구조를 꾸준히 개선해 나가야 한다.
- 구조 변경으로 인한 버그를 줄이면서 코드를 깔끔하게 유지할 수 있는 방법

---

## Chapter1. 이해하기 힘든 이름

- 좋은 이름은 어떤 역할을 하는지 어떻게 쓰이는지 직관적으로 나타내야한다.
- 사용할 수 있는 리팩터링 기술
    - 함수 선언 변경하기
    - 변수 이름 바꾸기
    - 필드 이름 바꾸기

### 함수 선언 변경하기
- 좋은 이름을 가진 함수는 어떻게 구현되었는지 이름만 보고도 이해할 수 있다.
    - 함수에 달린 주석을 기반으로 이름을 유추해보는 것도 좋은 방법
- 함수의 매개 변수는
    - 함수 내부의 문맥을 결정
    - 의존성을 결정

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>(); 

    private Set<String> reviews = new HashSet<>();

    private void studyReviews(GHIssue issue) throws IOException {
        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.studyReviews(issue);
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}
```