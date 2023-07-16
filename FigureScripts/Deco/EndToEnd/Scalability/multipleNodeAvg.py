import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px
import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[3268487.831, 3270697.149, 3291610.9, 3295282.32, 3308962.22, 3311921.619, 3298082.798, 3246734.899]
                         , line=dict(dash='dash', color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1745316.8, 1758634.4, 1731277.296, 1747254.91, 1740140.64, 1761900.8, 1775221.9, 1760157.36]
                         , line=dict(dash='dashdot', color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[8304926, 8398916, 8288468.03, 8394990, 8246141.96, 8281877.572, 8302153.97, 8389027.43]
                         , line=dict(dash='dot', color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Deco<sub>async</sub>", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[11714958.48, 18962738.22, 28444107.33, 37925476.44, 47406845.55, 56888214.66, 66369583.77, 75850952.88]
                         , line=dict(color=config.decoasy, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "20M", "40M", "60M", "80M"],
        tickvals=[0, 20000000, 40000000, 60000000, 80000000],
        tickmode="array",
        range=[0, 81000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="Local Nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5", "6", "7", "8"],
        tickvals=[1, 2, 3, 4, 5, 6, 7, 8],
        range=[1,8.2],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
# if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
#     os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/scalability\ThroughputAverageM.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/scalability\ThroughputAverageM.svg")
