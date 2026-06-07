import { traceApi } from './api';

/**
 * 全局前端异常收集与上报器 (errorTracker)
 * 
 * <p>自动监听 window 的 error 与 unhandledrejection 事件，
 * 获取报错文件、行列号、报错堆栈，并关联当前 SessionStorage 中最后一次 HTTP 请求的 traceId，发送至后端进行排查。</p>
 */
export const initErrorTracker = () => {
  if (typeof window === 'undefined') return;

  // 1. 捕获未处理的 JavaScript 运行时异常
  window.addEventListener('error', (event) => {
    if (!event.error) return;

    const errorLog = {
      message: event.message || event.error.message,
      filename: event.filename,
      lineno: event.lineno,
      colno: event.colno,
      stack: event.error.stack || '',
      url: window.location.href,
      userAgent: navigator.userAgent,
      traceId: sessionStorage.getItem('crs_last_trace_id') || ''
    };

    traceApi.reportError(errorLog).catch((err) => {
      console.error('Failed to report frontend error', err);
    });
  });

  // 2. 捕获未处理的 Promise Rejection
  window.addEventListener('unhandledrejection', (event) => {
    const error = event.reason;
    if (!error) return;

    const errorLog = {
      message: error.message || String(error),
      filename: error.fileName || '',
      lineno: error.lineNumber || 0,
      colno: error.columnNumber || 0,
      stack: error.stack || '',
      url: window.location.href,
      userAgent: navigator.userAgent,
      traceId: sessionStorage.getItem('crs_last_trace_id') || ''
    };

    traceApi.reportError(errorLog).catch((err) => {
      console.error('Failed to report unhandled Promise rejection', err);
    });
  });
};
