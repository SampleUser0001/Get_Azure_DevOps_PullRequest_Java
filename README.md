# Get_Azure_DevOps_PullRequest_Java
JavaでAzure DevOpsのプルリクエストを取得する。

- [Get\_Azure\_DevOps\_PullRequest\_Java](#get_azure_devops_pullrequest_java)
  - [実行](#実行)

## 実行

``` bash
# project_idの取得方法は下記を参照。
# https://sampleuser0001.github.io/cloud9_note/Azure/Azure_DevOps_API.html
repository_id=
pullrequest_id=
gradle run --args="${repository_id} ${pullrequest_id}"
```