package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;

    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // findAllメソッドを実行して、データの一覧を取得
        List<Task> tasks = taskDataAccess.findAll();

        // 所得したデータを表示する
        tasks.forEach(task -> {

            // User user = userDataAccess.findByCode(task);

            // 以下の条件分岐を追加
            String status = "未着手";
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

            System.out.println(task.getCode() + ". " + "担当者名 : " + task.getName() +
                    "が担当しています。" + ", ステータス : " + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code        タスクコード
     * @param name        タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser   ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
    User loginUser) throws AppException {

        Task task = new Task(code, name, repUserCode, loginUser);

        if(task == null){
            throw new AppException("存在するユーザーコードを入力してください");
        }

        // saveメソッドを呼び出して、入力されたデータを保存
        taskDataAccess.save(task);
        System.out.println("商品の登録が完了しました。");

        // List<Log> logs = logDataAccess.save();

    }


    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code      タスクコード
     * @param status    新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
    User loginUser) throws AppException {

        Task task = taskDataAccess.findByCode(code);

                // 進行状況変数
                int taskStatus = task.getStatus();

                if (status == 2) {
                    throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
                } else if (status == 1) {
                    if (taskStatus == 1 || taskStatus == 2) {
                        throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
                    }
                } else if (status == 0) {
                    if (taskStatus == 0 || taskStatus == 2) {
                        throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
                    }
                }

         // マッピング
            Task task2 = new Task(task.getCode(), task.getName(), taskStatus, loginUser);
            // csvへ書き込み
            taskDataAccess.update(task2);
            // logマッピング
            Log log = new Log(task.getCode(),
                    loginUser.getCode(),
                    task.getStatus(),
                    LocalDate.now());
            // csvへ書き込み
            logDataAccess.save(log);
            System.out.println("ステータスの変更が完了しました。");
        }
    

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}