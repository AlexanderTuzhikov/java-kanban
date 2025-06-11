public class Subtask extends Task {
    private final int epicId;

    public Subtask(int taskId, String taskName, String taskInfo, int epicId) {
        super(taskId, taskName, taskInfo);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskInfo='" + getTaskInfo() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
