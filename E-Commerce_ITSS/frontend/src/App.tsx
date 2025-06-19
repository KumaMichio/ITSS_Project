import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import Home from "./views/Home/Home";
import Checkout from "./views/Checkout/Checkout";
import Cart from "./views/Cart/Cart";
import Shop from "./views/Shop/Shop";
import Login from "./views/Auth/Login";
import Register from "./views/Auth/Register";
import GuestOrderTest from "./views/Test/GuestOrderTest";
import TransactionResult from "./views/Transaction/TransactionResult";
import { AuthProvider } from "./contexts/AuthContext";
import { CartProvider } from "./contexts/CartContext";
import ErrorBoundary from "./components/ErrorBoundary";

function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <CartProvider>
          <div className="w-full min-h-screen bg-[#E5E5E5]">            <Router>              <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/shop" element={<Shop />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/test-guest-order" element={<GuestOrderTest />} />
            <Route path="/transaction/success" element={<TransactionResult />} />
            <Route path="/transaction/fail" element={<TransactionResult />} />
          </Routes>
          </Router>
          </div>
        </CartProvider>
      </AuthProvider>
    </ErrorBoundary>
  )
}

export default App
