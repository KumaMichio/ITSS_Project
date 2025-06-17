import React, { useState } from 'react';
import Header from '../../components/Header';
import { testGuestOrderWorkflow, testGuestOrderFallback } from '../../utils/testGuestOrder';

const GuestOrderTest: React.FC = () => {
    const [testResults, setTestResults] = useState<string[]>([]);
    const [isRunning, setIsRunning] = useState(false);

    const addResult = (message: string) => {
        setTestResults(prev => [...prev, `${new Date().toLocaleTimeString()}: ${message}`]);
    }; const runWorkflowTest = async () => {
        setIsRunning(true);
        addResult('🚀 Starting guest order workflow test...');

        try {
            // Capture console logs to display in UI
            const originalLog = console.log;
            console.log = (...args) => {
                addResult(args.join(' '));
                originalLog.apply(console, args);
            };

            await testGuestOrderWorkflow();
            addResult('✅ Workflow test completed successfully');

            // Restore original console.log
            console.log = originalLog;
        } catch (error) {
            addResult(`❌ Workflow test failed: ${error}`);
        }

        setIsRunning(false);
    };

    const runFallbackTest = async () => {
        setIsRunning(true);
        addResult('🔄 Starting fallback test...');

        try {
            // Capture console logs
            const originalLog = console.log;
            console.log = (...args) => {
                addResult(args.join(' '));
                originalLog.apply(console, args);
            };

            await testGuestOrderFallback();
            addResult('✅ Fallback test completed successfully');

            // Restore original console.log
            console.log = originalLog;
        } catch (error) {
            addResult(`❌ Fallback test failed: ${error}`);
        }

        setIsRunning(false);
    };

    const clearResults = () => {
        setTestResults([]);
    };

    return (
        <>
            <Header />
            <div className="max-w-4xl mx-auto p-8 mt-10">
                <h1 className="text-3xl font-bold text-center mb-8">Guest Order Testing</h1>

                <div className="bg-white p-6 rounded-lg shadow-lg mb-8">
                    <h2 className="text-xl font-semibold mb-4">Test Functions</h2>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                        <button
                            onClick={runWorkflowTest}
                            disabled={isRunning}
                            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
                        >
                            {isRunning ? 'Running...' : 'Test Complete Workflow'}
                        </button>

                        <button
                            onClick={runFallbackTest}
                            disabled={isRunning}
                            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:bg-gray-400"
                        >
                            {isRunning ? 'Running...' : 'Test Fallback Mode'}
                        </button>

                        <button
                            onClick={clearResults}
                            disabled={isRunning}
                            className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700 disabled:bg-gray-400"
                        >
                            Clear Results
                        </button>
                    </div>
                </div>

                <div className="bg-white p-6 rounded-lg shadow-lg">
                    <h2 className="text-xl font-semibold mb-4">Test Results</h2>

                    <div className="bg-black text-green-400 p-4 rounded font-mono text-sm max-h-96 overflow-y-auto">
                        {testResults.length === 0 ? (
                            <div className="text-gray-500">No test results yet. Click a test button above.</div>
                        ) : (
                            testResults.map((result, index) => (
                                <div key={index} className="mb-1">
                                    {result}
                                </div>
                            ))
                        )}
                    </div>
                </div>

                <div className="mt-8 bg-blue-50 p-6 rounded-lg">
                    <h3 className="text-lg font-semibold mb-3">Guest Order Features</h3>
                    <ul className="space-y-2 text-sm">
                        <li>✅ <strong>Guest Checkout:</strong> Customers can place orders without creating accounts</li>
                        <li>✅ <strong>Stock Validation:</strong> Real-time inventory checking before order placement</li>
                        <li>✅ <strong>Dynamic Shipping:</strong> Location and weight-based shipping calculation</li>
                        <li>✅ <strong>Express Delivery:</strong> 2-hour delivery option for eligible areas (Hanoi inner city)</li>
                        <li>✅ <strong>VNPay Integration:</strong> Payment processing and refund support</li>
                        <li>✅ <strong>Email Notifications:</strong> Order confirmation with cancellation links</li>
                        <li>✅ <strong>Fallback Support:</strong> Works offline with demo data</li>
                    </ul>
                </div>

                <div className="mt-8 bg-yellow-50 p-6 rounded-lg">
                    <h3 className="text-lg font-semibold mb-3">Backend Services Status</h3>
                    <ul className="space-y-2 text-sm">
                        <li>🔧 <strong>GuestOrderController:</strong> API endpoints for guest checkout</li>
                        <li>🔧 <strong>StockValidationService:</strong> Inventory management and validation</li>
                        <li>🔧 <strong>ShippingCalculatorService:</strong> Complex shipping fee calculation</li>
                        <li>🔧 <strong>EmailService:</strong> Order confirmation and status updates</li>
                        <li>🔧 <strong>VNPay Integration:</strong> Payment and refund processing</li>
                    </ul>
                </div>
            </div>
        </>
    );
};

export default GuestOrderTest;
