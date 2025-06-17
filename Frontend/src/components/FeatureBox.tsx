import React from "react";

interface FeatureBoxProps {
    img: string;
    label: string;
    type: string;
    isActive?: boolean;
    onClick?: () => void;
}

const FeatureBox: React.FC<FeatureBoxProps> = ({ img, label, type, isActive, onClick }) => {
    return (
        <div
            className={`w-28 text-center p-6 rounded shadow border border-[#cce7d0] cursor-pointer transition hover:shadow-lg ${isActive ? 'bg-[#e3e6f2]' : 'bg-white'
                }`}
            onClick={onClick}
            data-type={type}
        >
            <img src={img} alt={label} className="w-24 h-24 object-contain mx-auto mb-2" />
            <h6 className="font-bold text-xs">{label}</h6>
        </div>
    );
};

export default FeatureBox;
