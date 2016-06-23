#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <../jpeg8d/jpeglib.h>
#include <setjmp.h>

#define  LOG_TAG    "MJPEG"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define abs_mcr(x) ((x)>0 ? (x) : -(x))

int IMG_WIDTH=-1;
int IMG_HEIGHT=-1;

/* for libjpeg */
typedef struct {
  struct jpeg_source_mgr pub;/* public fields */
  int skip;
} memory_source_mgr;
typedef memory_source_mgr *memory_src_ptr;

struct my_error_mgr {
  struct jpeg_error_mgr pub;
  jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr * my_error_ptr;

METHODDEF(void) my_error_exit (j_common_ptr cinfo);
GLOBAL(void) jpeg_memory_src (j_decompress_ptr cinfo, void* data, unsigned long len);
METHODDEF(void) memory_init_source (j_decompress_ptr cinfo);
METHODDEF(boolean) memory_fill_input_buffer (j_decompress_ptr cinfo);
METHODDEF(void) memory_skip_input_data (j_decompress_ptr cinfo, long num_bytes);
METHODDEF(void) memory_term_source (j_decompress_ptr cinfo);
/* end of libjpeg */


int processimage (const void *p, int l, int *rgbbuf, int w, int h);
int Java_it_zm_mobile_mjpeg_MjpegInputStream_pixeltorgb(JNIEnv* env,jobject thiz, jbyteArray jp, jint l, jintArray ji, jint w, jint h);

