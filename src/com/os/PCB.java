package com.os;

import com.config.ConstantTime;
import com.config.InstructionStatus;
import com.config.ProcessStatus;
import com.hardware.CPU;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PCB {
    private int ProID;//值分别为 1,2,3,4,5,6，。。
    private int Priority;//优先级
    private int InTimes;//进程创建时间
    private int StartTime;//进程开始时间
    private int EndTimes;//进程结束时间
    private int InstructionsNum;//进程包含的指令数目
    private int IR = 0;//正在执行的指令编号：Instruc_ID
    private int PC = 1;//下一条将执行的指令编号：Instruc_ID
    private int timeSliceLeft;//时间片剩余时间
    public PCBInReadyQueue pcbInReadyQueue = new PCBInReadyQueue();
    public PCBInKeyBoardInputQueue pcbInKeyBoardInputQueue = new PCBInKeyBoardInputQueue();
    public PCBInPVOperationQueue pcbInPVOperationQueue = new PCBInPVOperationQueue();
    public PCBInScreenOutputInputQueue pcbInScreenOutputInputQueue = new PCBInScreenOutputInputQueue();
    private ProcessStatus psw;//进程状态：运行、阻塞、就绪
    public ArrayList<PCBInstructions> pcbInstructions = new ArrayList<PCBInstructions>();//包含的指令list

    public PCBInstructions getCurrentInstruction() {
        return this.pcbInstructions.get(this.IR);
    }

    public PCBInstructions getInstructionById(int no) {
        return this.pcbInstructions.get(no);
    }

    public PCB() {
        this.timeSliceLeft = ConstantTime.TIME_SLICE;
    }

    //进程在队列中的信息--抽象类
    private abstract static class PCBInQueue {
        int BqNum;//编号
        int BqTime;//进入队列时间

        public int getBqNum() {
            return BqNum;
        }

        public int getBqTime() {
            return BqTime;
        }

        public void setBqNum(int num) {
            this.BqNum = num;
        }

        public void setBqTime(int time) {
            this.BqTime = time;
        }
    }

    public static class PCBInReadyQueue extends PCBInQueue {
    } //就绪队列信息

    public static class PCBInKeyBoardInputQueue extends PCBInQueue {
    } //键盘输入阻塞队列信息

    public static class PCBInScreenOutputInputQueue extends PCBInQueue {
    } //屏幕输出阻塞队列信息

    public static class PCBInPVOperationQueue extends PCBInQueue {
    } //PV操作阻塞队列信息

    public InstructionStatus getInstructionState() {
        return pcbInstructions.get(this.IR).getInstructionState();
    }

    public int getStartTime() {
        return StartTime;
    }

    public void setStartTime(int startTime) {
        StartTime = startTime;
    }

    public int getEndTimes() {
        return EndTimes;
    }

    public ArrayList<PCBInstructions> getPcbInstructions() {
        return pcbInstructions;
    }

    public void setPcbInstructions(ArrayList<PCBInstructions> pcbInstructions) {
        this.pcbInstructions = pcbInstructions;
    }

    public int getTimeSliceLeft() {
        return timeSliceLeft;
    }

    public void setTimeSliceLeft(int timeSliceLeft) {
        this.timeSliceLeft = timeSliceLeft;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getIR() {
        return IR;
    }

    public void AddCurrentIR() {
        this.IR++;
    }

    public void AddCurrentPC() {
        this.PC++;
    }

    public void setIR(int IR) {
        this.IR = IR;
    }

    public int getInstructionsNum() {
        return InstructionsNum;
    }

    public void setInstructionsNum(int instructionsNum) {
        InstructionsNum = instructionsNum;
    }

    public void setEndTimes(int endTimes) {
        EndTimes = endTimes;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public void setProID(int proID) {
        ProID = proID;
    }

    public void setInTimes(int inTimes) {
        InTimes = inTimes;
    }

    public void setInstrucNum(int instrucNum) {
        InstructionsNum = instrucNum;
    }


    public int getInstrucNum() {
        return InstructionsNum;
    }

    public int getInTimes() {
        return InTimes;
    }

    public int getInClockTime() {
        return InTimes * 1000;
    }

    public int getPriority() {
        return Priority;
    }

    public int getProID() {
        return ProID;
    }

    public ProcessStatus getPsw() {
        return psw;
    }

    public void setPsw(ProcessStatus psw) {
        this.psw = psw;
    }

    //获取进程已运行时间
    public int getPCBCurrentRunTime() {
        int currentTotalRunTime = 0;
        for (int i = 0; i < this.getIR(); i++) {
            switch (this.getInstructionById(i).getInstructionState()) {
                case USERMODE_CALC:
                    currentTotalRunTime += 1;
                    break;
                case PV_OPERATION:
                    currentTotalRunTime += 2;
                    break;
                case SCREEN_DISPLAY:
                    currentTotalRunTime += 3;
                    break;
                case KEYBOARD_INPUT:
                    currentTotalRunTime += 4;
                    break;
            }
        }
        return currentTotalRunTime;
    }

    //获取进程总共需要的运行时间
    public int getPCBRunTotalNeedTime() {
        int totalRunTime = 0;
        for (int i = 0; i < this.getInstrucNum(); i++) {
            switch (this.getInstructionById(i).getInstructionState()) {
                case KEYBOARD_INPUT:
                    totalRunTime += 4;
                    break;
                case SCREEN_DISPLAY:
                    totalRunTime += 3;
                    break;
                case PV_OPERATION:
                    totalRunTime += 2;
                    break;
                case USERMODE_CALC:
                    totalRunTime += 1;
                    break;
            }
        }
        return totalRunTime;
    }

    //进程完全运行结束
    public boolean isPCBRunOver() {
        return this.getPCBCurrentRunTime() >= this.getPCBRunTotalNeedTime();
    }

    //进程一个时间片运行完
    public boolean isPCBRunOverInTimeSlice() {
        return this.timeSliceLeft == 0;
    }

    public void displayProcess() {
        System.out.print("进程id：" + this.getProID() + " ");
    }

    public void createProcess() {
        //进程创建
        //将该进程设置为就绪态，加入到就绪队列
        this.setPsw(ProcessStatus.Ready);
        PCBPool.pcbPool.AddProcess2ReadyQueue(this);
    }

    public void destroyProcess() {
        PCBPool.pcbPool.deletePCBFromPool(this);
        PCBPool.pcbPool.getReadyQueue().remove(this);
        this.setEndTimes(ConstantTime.getSystemTime());
        CPU.cpu.DeleteRunningPCB();
        System.out.println("已从PCB池中删除此进程");

    }//进程撤销,执行完成的进程调用撤销函数；

    public void blockProcess(@NotNull InstructionStatus instructionStatus) {
        //进程原语：进程阻塞,就绪--》阻塞
        PCBPool.pcbPool.getReadyQueue().remove(this);
        switch (instructionStatus) {
            case PV_OPERATION:
                //加入阻塞队列
                PCBPool.pcbPool.AddProcess2PVBlockQueue(this);
                //PlatForm.platForm.pvBlockInfo.setText(PCBPool.pcbPool.concatList2String(PCBPool.pcbPool.getPvBlockQueue()));
                break;
            case SCREEN_DISPLAY:
                PCBPool.pcbPool.AddProcess2OutputBlockQueue(this);
                //PlatForm.platForm.outputBlockInfo.setText(PCBPool.pcbPool.concatList2String(PCBPool.pcbPool.getOutputBlockQueue()));
                break;
            case KEYBOARD_INPUT:
                PCBPool.pcbPool.AddProcess2InputBlockQueue(this);
                //PlatForm.platForm.inputBlockInfo.setText(PCBPool.pcbPool.concatList2String(PCBPool.pcbPool.getInputBlockQueue()));
                break;
            default:
                break;
        }
        this.setPsw(ProcessStatus.Block);
    }//进程阻塞,进程切换、CPU 模式切换时调用

    //进程唤醒
    public void wakeProcess(ArrayList<PCB> back2ReadyQueue) {
        PCBPool.pcbPool.AddProcess2ReadyQueue(this);
        back2ReadyQueue.remove(this);
        System.out.println("进程：" + this.getProID() + "，已回到就绪队列");
        this.setPsw(ProcessStatus.Ready);
    }

    public boolean isPCBOver() {
        return this.getIR() == this.getInstrucNum();
    }

    public void NormalInstrucRun() {
        System.out.println("进程正常调度");
        //时间片减一
        this.timeSliceLeft--;
        this.AddCurrentIR();
        this.AddCurrentPC();
    }

    public void InputInstrucRun() {
        //进入阻塞队列
        this.blockProcess(this.getInstructionState());
        //终止当前运行的进程
        CPU.cpu.DeleteRunningPCB();
        //遇到中断，先保护现场
        CPU.cpu.Protect(this);

    }

    public void OutPutInstrucRun() {
        //进入阻塞队列
        this.blockProcess(this.getInstructionState());
        CPU.cpu.DeleteRunningPCB();
        //遇到中断，先保护现场
        CPU.cpu.Protect(this);
    }

    public void PVCommunicateInstrucRun() {
        //PV通信阻塞指令；关时间中断
        //进入阻塞队列
        this.blockProcess(this.getInstructionState());
        CPU.cpu.DeleteRunningPCB();
        //遇到中断，先保护现场
        CPU.cpu.Protect(this);
    }
}
