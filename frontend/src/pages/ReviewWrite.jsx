import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';
import { useTranslation } from 'react-i18next';

function ReviewWrite() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  
  const [liquor, setLiquor] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  // Basic Form State
  const [rating, setRating] = useState(5);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [tagsInput, setTagsInput] = useState('');
  
  // Detail Form State
  const [details, setDetails] = useState({});
  
  // Images
  const [images, setImages] = useState([]);
  const [previewUrls, setPreviewUrls] = useState([]);

  useEffect(() => {
    const fetchLiquor = async () => {
      try {
        const response = await fetch(`/api/liquors/${id}`);
        if (!response.ok) throw new Error('Liquor not found');
        const data = await response.json();
        setLiquor(data);
        
        // Initialize default detail values based on category
        if (data.category === 'BEER') {
          setDetails({ aroma: 3, taste: 3, headRetention: 3, look: 3 });
        } else if (data.category === 'WINE') {
          setDetails({ sweetness: 3, acidity: 3, body: 3, tannin: 3 });
        } else if (data.category === 'WHISKY') {
          setDetails({ aroma: 3, taste: 3, finish: 3, body: 3 });
        }
      } catch (err) {
        console.error(err);
        window.alert('Failed to load liquor information.');
        navigate(-1);
      } finally {
        setIsLoading(false);
      }
    };
    
    fetchLiquor();
  }, [id, navigate]);

  const handleDetailChange = (field, value) => {
    setDetails(prev => ({ ...prev, [field]: parseFloat(value) }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImages(prev => [...prev, ...files]);
    
    const newUrls = files.map(file => URL.createObjectURL(file));
    setPreviewUrls(prev => [...prev, ...newUrls]);
  };

  const removeImage = (index) => {
    setImages(prev => prev.filter((_, i) => i !== index));
    setPreviewUrls(prev => {
      URL.revokeObjectURL(prev[index]);
      return prev.filter((_, i) => i !== index);
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!title.trim() || !content.trim()) {
      window.alert(t('reviewWrite.fillRequired'));
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('reviewWrite.loginRequired'));
        navigate('/signin');
        return;
      }

      // Process tags
      const tags = tagsInput.split(',').map(t => t.trim()).filter(t => t);
      
      // Build request payload
      const requestPayload = {
        rating,
        title,
        content,
        tags: tags.slice(0, 10), // Max 10 tags
      };

      // Add category-specific details
      if (liquor.category === 'BEER' || liquor.category === 'WINE' || liquor.category === 'WHISKY') {
        requestPayload.reviewDetailRequest = {
          type: liquor.category,
          ...details
        };
      }
      
      const formData = new FormData();
      formData.append(
        'request',
        new Blob([JSON.stringify(requestPayload)], { type: 'application/json' })
      );
      
      images.forEach(file => {
        formData.append('images', file);
      });

      const response = await fetch(`/api/liquors/${id}/reviews`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        },
        body: formData
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to submit review');
      }

      window.alert(t('reviewWrite.submitSuccess'));
      navigate(`/liquors/${id}`);
      
    } catch (err) {
      console.error(err);
      window.alert(`Error: ${err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-32">
        <svg className="animate-spin h-12 w-12 text-amber-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
      </div>
    );
  }

  const renderDetailSliders = () => {
    let fields = [];
    if (liquor.category === 'BEER') {
      fields = [
        { key: 'aroma', label: t('reviewWrite.beerFields.aroma') },
        { key: 'taste', label: t('reviewWrite.beerFields.taste') },
        { key: 'headRetention', label: t('reviewWrite.beerFields.headRetention') },
        { key: 'look', label: t('reviewWrite.beerFields.look') }
      ];
    } else if (liquor.category === 'WINE') {
      fields = [
        { key: 'sweetness', label: t('reviewWrite.wineFields.sweetness') },
        { key: 'acidity', label: t('reviewWrite.wineFields.acidity') },
        { key: 'body', label: t('reviewWrite.wineFields.body') },
        { key: 'tannin', label: t('reviewWrite.wineFields.tannin') }
      ];
    } else if (liquor.category === 'WHISKY') {
      fields = [
        { key: 'aroma', label: t('reviewWrite.whiskyFields.aroma') },
        { key: 'taste', label: t('reviewWrite.whiskyFields.taste') },
        { key: 'finish', label: t('reviewWrite.whiskyFields.finish') },
        { key: 'body', label: t('reviewWrite.whiskyFields.body') }
      ];
    }

    if (fields.length === 0) return null;

    return (
      <div className="bg-slate-50 p-6 rounded-2xl border border-slate-100 mb-8 shadow-sm">
        <h3 className="text-lg font-bold text-slate-800 mb-4">{liquor.category} {t('reviewWrite.tastingNotes')}</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {fields.map(field => (
            <div key={field.key}>
              <div className="flex justify-between items-center mb-2">
                <label className="text-sm font-semibold text-slate-700">{field.label}</label>
                <span className="text-sm font-bold text-amber-600 bg-amber-100 px-2 py-0.5 rounded">
                  {details[field.key] || 3} / 5
                </span>
              </div>
              <input 
                type="range" 
                min="0" 
                max="5" 
                step="0.5" 
                value={details[field.key] || 3}
                onChange={(e) => handleDetailChange(field.key, e.target.value)}
                className="w-full accent-amber-500 bg-slate-200 rounded-lg appearance-none h-2"
              />
              <div className="flex justify-between text-xs text-slate-400 mt-1 font-medium">
                <span>{t('reviewWrite.low')}</span>
                <span>{t('reviewWrite.high')}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-12 animate-fade-in-up">
      <button 
        onClick={() => navigate(-1)}
        className="mb-6 flex items-center text-slate-500 hover:text-amber-600 font-medium transition-colors group"
      >
        <svg className="w-5 h-5 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        {t('reviewWrite.cancelReview')}
      </button>

      <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
        {/* Header with Liquor Info */}
        <div className="bg-slate-50 border-b border-slate-200 p-6 flex items-center gap-6">
          {liquor.imageUrl ? (
            <img 
              src={liquor.imageUrl} 
              alt={liquor.name} 
              className="w-20 h-20 object-contain drop-shadow-md bg-white rounded-xl p-2 border border-slate-200" 
              onError={(e) => { e.target.src = '/default-liquor.svg' }}
            />
          ) : (
            <img 
              src="/default-liquor.svg" 
              alt={liquor.name} 
              className="w-20 h-20 object-contain drop-shadow-md bg-white rounded-xl p-2 border border-slate-200" 
            />
          )}
          <div>
            <span className="px-2 py-0.5 bg-amber-100 text-amber-800 text-xs font-bold uppercase tracking-wider rounded">
              {liquor.category}
            </span>
            <h1 className="text-2xl font-extrabold text-slate-900 mt-1">{t('reviewWrite.reviewLabel')} {liquor.name}</h1>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-8 md:p-10">
          
          {/* Overall Rating */}
          <div className="mb-8 flex flex-col items-center">
            <label className="text-sm font-bold text-slate-400 uppercase tracking-wider mb-3">{t('reviewWrite.overallRating')}</label>
            <div className="flex items-center gap-6 w-full max-w-md">
              <span className="text-2xl font-bold text-slate-400">1</span>
              <div className="flex-1 flex flex-col relative">
                <input 
                  type="range" 
                  min="1" 
                  max="10" 
                  step="1"
                  value={rating} 
                  onChange={(e) => setRating(parseInt(e.target.value))}
                  className="w-full accent-amber-500 h-3 bg-slate-200 rounded-lg appearance-none z-10 relative"
                />
                <div className="absolute top-6 left-1/2 -translate-x-1/2 bg-amber-500 text-white font-bold px-3 py-1 rounded-lg text-xl shadow-md pointer-events-none">
                  {rating}
                </div>
              </div>
              <span className="text-2xl font-bold text-slate-400">10</span>
            </div>
            <div className="mt-12 w-full border-t border-slate-100"></div>
          </div>

          {renderDetailSliders()}

          <div className="space-y-6">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">{t('reviewWrite.reviewTitle')} <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                required
                placeholder={t('reviewWrite.reviewTitlePlaceholder')}
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all font-medium text-slate-800"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">{t('reviewWrite.reviewContent')} <span className="text-red-500">*</span></label>
              <textarea 
                required
                rows="6"
                placeholder={t('reviewWrite.reviewContentPlaceholder')}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                className="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all resize-y text-slate-800"
              ></textarea>
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">{t('reviewWrite.tags')}</label>
              <input 
                type="text" 
                placeholder={t('reviewWrite.tagsPlaceholder')}
                value={tagsInput}
                onChange={(e) => setTagsInput(e.target.value)}
                className="w-full px-4 py-3 rounded-xl border border-slate-200 bg-slate-50 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all text-slate-800"
              />
              <p className="text-xs text-slate-400 mt-2">{t('reviewWrite.tagsHint')}</p>
            </div>

            {/* Images */}
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-3">{t('reviewWrite.addPhotos')}</label>
              
              <div className="flex flex-wrap gap-4">
                {previewUrls.map((url, idx) => (
                  <div key={idx} className="relative group rounded-xl overflow-hidden border border-slate-200 w-24 h-24">
                    <img src={url} alt={`Preview ${idx}`} className="w-full h-full object-cover" />
                    <button 
                      type="button"
                      onClick={() => removeImage(idx)}
                      className="absolute inset-0 bg-black/50 text-white flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                    </button>
                  </div>
                ))}
                
                <label className="w-24 h-24 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50 hover:bg-slate-100 hover:border-amber-400 flex flex-col items-center justify-center text-slate-400 hover:text-amber-500 transition-colors cursor-pointer">
                  <svg className="w-8 h-8 mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" /></svg>
                  <span className="text-xs font-semibold">{t('reviewWrite.upload')}</span>
                  <input 
                    type="file" 
                    multiple 
                    accept="image/*" 
                    onChange={handleImageChange}
                    className="hidden"
                  />
                </label>
              </div>
            </div>
          </div>

          <div className="mt-10 flex justify-end">
            <button 
              type="submit"
              disabled={isSubmitting}
              className={`bg-amber-500 hover:bg-amber-600 text-white font-bold py-4 px-10 rounded-xl transition-all duration-200 shadow-md flex items-center gap-2 ${isSubmitting ? 'opacity-70 cursor-not-allowed' : 'hover:-translate-y-1'}`}
            >
              {isSubmitting ? (
                <>
                  <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Submitting...
                </>
              ) : (
                <>
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" /></svg>
                  {t('reviewWrite.submitReview')}
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ReviewWrite;
