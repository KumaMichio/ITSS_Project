import { Link, useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { useCart } from "../contexts/CartContext";
import logo from "../img/logo.png";

const Header = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { user, isAuthenticated, logout } = useAuth();
    const { totalItems } = useCart();
    const [searchQuery, setSearchQuery] = useState("");

    const isActive = (path: string) => location.pathname === path; const handleLogout = () => {
        logout();
        // Optionally redirect to home
        window.location.href = '/';
    };

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/shop?search=${encodeURIComponent(searchQuery.trim())}`);
        }
    };

    return (
        <section className="flex items-center justify-between px-5 py-2 bg-[#e3e6f2] shadow-md sticky top-0 z-50">
            <Link to="/">
                <img src={logo} className="h-12" alt="logo" />
            </Link>

            {/* Search Bar */}
            <form onSubmit={handleSearch} className="flex items-center flex-1 max-w-md mx-8">
                <div className="relative w-full">
                    <input
                        type="text"
                        placeholder="Tìm kiếm sản phẩm..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#088178] focus:border-transparent"
                    />
                    <button
                        type="submit"
                        className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-[#088178] transition"
                    >
                        <i className="fas fa-search"></i>
                    </button>
                </div>
            </form>

            <ul className="flex items-center space-x-6 font-semibold">
                <li>
                    <Link
                        to="/"
                        className={`hover:text-[#088178] transition ${isActive('/') ? 'text-[#088178] border-b-2 border-[#088178]' : ''}`}
                    >
                        Home
                    </Link>
                </li>
                <li>
                    <Link
                        to="/shop"
                        className={`hover:text-[#088178] transition ${isActive('/shop') ? 'text-[#088178] border-b-2 border-[#088178]' : ''}`}
                    >
                        Shop
                    </Link>
                </li>

                {/* Cart with item count */}
                <li id="lg-bag" className="relative">
                    <Link
                        to="/cart"
                        className={`hover:text-[#088178] transition ${isActive('/cart') ? 'text-[#088178] border-b-2 border-[#088178]' : ''}`}
                    >
                        <i className="fa-solid fa-cart-shopping"></i>
                        {totalItems > 0 && (
                            <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                                {totalItems > 99 ? '99+' : totalItems}
                            </span>
                        )}
                    </Link>
                </li>

                {/* Authentication */}
                {isAuthenticated ? (
                    <li className="flex items-center space-x-3">
                        <span className="text-sm text-gray-600">
                            Xin chào, {user?.username}
                        </span>
                        <button
                            onClick={handleLogout}
                            className="text-sm hover:text-[#088178] transition"
                        >
                            Đăng xuất
                        </button>
                    </li>
                ) : (
                    <li className="flex items-center space-x-3">
                        <Link
                            to="/login"
                            className="text-sm hover:text-[#088178] transition"
                        >
                            Đăng nhập
                        </Link>
                        <span className="text-gray-400">|</span>
                        <Link
                            to="/register"
                            className="text-sm hover:text-[#088178] transition"
                        >
                            Đăng ký
                        </Link>
                    </li>
                )}
            </ul>
        </section>
    );
};

export default Header;
